/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagPhotoFacadeLocal;
import net.wazari.dao.TagThemeFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.jpa.entity.xml.WebAlbumsXML;
import net.wazari.common.util.XmlUtils ;
import net.wazari.dao.*;
import net.wazari.dao.DatabaseFacadeLocal.DatabaseFacadeLocalException;
import net.wazari.dao.entity.*;
import net.wazari.dao.jpa.entity.*;
/**
 *
 * @author kevinpouget
 */
@Stateless
public class XMLImportExport implements ImportExporter {
    private static final Logger log = LoggerFactory.getLogger(XMLImportExport.class.getName());

    private static final String FILENAME = "WebAlbums."+WebAlbumsDAOBean.PERSISTENCE_UNIT+".xml" ;
    @EJB
    private TagThemeFacadeLocal tagThemeDAO;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private TagPhotoFacadeLocal tagPhotoDAO;
    @EJB
    private TagFacadeLocal tagDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;
    @EJB
    private AlbumFacadeLocal albumDAO;
    @EJB
    private PhotoFacadeLocal photoDAO ;
    @EJB
    private CarnetFacadeLocal carnetDAO ;
    
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    public void exportXml(String path) throws DatabaseFacadeLocalException {
        WebAlbumsXML web = new WebAlbumsXML(themeDAO.findAll(), userDAO.findAll(),
                tagDAO.findAll(), carnetDAO.findAll()) ;
        try {
            XmlUtils.save(new File(path+FILENAME), web, WebAlbumsXML.clazzez);
            log.info( "XML Saved! into "+path+FILENAME);
        } catch (JAXBException ex) {
            log.error("JAXBException", ex);
            throw new DatabaseFacadeLocalException(ex);
        }
    }

    @Override
    public void importXml(String path) throws DatabaseFacadeLocalException {
        try {
            WebAlbumsXML web = XmlUtils.reload(new FileInputStream(new File(path+FILENAME)), WebAlbumsXML.clazzez);
            if (web == null) {
                log.warn("Couldn't load the XML backup file "+path+FILENAME+"...");
                return ;
            }
            
            Map<Integer, JPATag> tags = new HashMap<Integer, JPATag>(web.Tags.size()) ;
            Map<Integer, JPAPhoto> photos = new HashMap<Integer, JPAPhoto>() ;
            Map<Integer, JPATheme> themes = new HashMap<Integer, JPATheme>(web.Themes.size()) ;
            Map<Integer, JPAAlbum> albums = new HashMap<Integer, JPAAlbum>() ;
            Map<Integer, JPAUtilisateur> users = new HashMap<Integer, JPAUtilisateur>(web.Utilisateurs.size()) ;

            // only way to put that *** in the database !
            em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0;").executeUpdate();
            
            log.warn( "Put entities in the cache") ;
            if (web.Themes != null) {
                for (JPATheme enrTheme : web.Themes) {
                    if (enrTheme.getAlbumList() != null) {
                        for (JPAAlbum enrAlbum : (List<JPAAlbum>) enrTheme.getAlbumList()) {
                            albums.put(enrAlbum.getId(), enrAlbum);
                            
                            if (enrAlbum.getPhotoList() != null) {
                                for (JPAPhoto enrPhoto : (List<JPAPhoto>) enrAlbum.getPhotoList()) {
                                    photos.put(enrPhoto.getId(), enrPhoto);
                                }
                            }
                        }
                    }
                }
            }
            if (web.Utilisateurs != null) {
                for (JPAUtilisateur enrUser : web.Utilisateurs) {
                    users.put(enrUser.getId(), em.merge(enrUser)) ;
                }            
            }
            
            log.warn( "Process entities") ;
            if (web.Tags != null) {
                log.warn( "Import {} Tag",web.Tags.size()) ;
                for (JPATag enrTag : web.Tags) {
                    if (enrTag.getGeolocalisation() != null) {
                        enrTag.getGeolocalisation().setTag(enrTag);
                        em.persist(enrTag.getGeolocalisation());
                    }

                    if (enrTag.getPerson() != null) {
                        enrTag.getPerson().setTag(enrTag);
                        em.persist(enrTag.getPerson());
                    }
                    
                    tags.put(enrTag.getId(), em.merge(enrTag)) ;
                }
                
                log.warn("Second pass for the tags");
                for (JPATag enrTag : web.Tags) {
                    if (enrTag.parentId != null) {
                        JPATag enrTagParent = tags.get(enrTag.parentId);
                        enrTag.setParent(enrTagParent);
                        if (enrTagParent.getSonList() == null) 
                            enrTagParent.setSonList(new LinkedList<Tag>());
                        enrTagParent.getSonList().add(enrTag);
                    }
                    em.merge(enrTag);
                }
            }
            
            if (web.Themes != null) {
                for (JPATheme enrTheme : web.Themes) {
                    log.info( "Theme: {}", enrTheme.getNom()) ;                    
                    
                    if (enrTheme.getCarnetList() != null) {
                        for (JPACarnet enrCarnet : (List<JPACarnet>) enrTheme.getCarnetList()) {
                            log.info( "Carnet: {}", enrCarnet.getNom()) ;
                            
                            if (enrCarnet.droitId != null) {
                                enrCarnet.setDroit(users.get(enrCarnet.droitId));
                            }
                            
                            enrCarnet.setTheme(enrTheme);
                            em.persist(enrCarnet);
                        }
                    }
                    
                    if (enrTheme.getAlbumList() != null) {
                        for (JPAAlbum enrAlbum : (List<JPAAlbum>) enrTheme.getAlbumList()) {
                            log.info( "Album: {}", enrAlbum.getNom()) ;

                            enrAlbum.setTheme(enrTheme);
                            Utilisateur enrUser = users.get(enrAlbum.droitId);
                            enrAlbum.setDroit(enrUser);
                            if (enrUser.getAlbumList() == null)
                                enrUser.setAlbumList(new LinkedList<Album>());
                            enrUser.getAlbumList().add(enrAlbum);
                            
                            if (enrAlbum.getGpxList() != null) {
                                for (JPAGpx enrGpx : (List<JPAGpx>) enrAlbum.getGpxList()) {
                                    enrGpx.setAlbum(enrAlbum);
                                    em.merge(enrGpx);
                                }
                            }
                            
                            if (enrAlbum.getPhotoList() != null) {
                                for (JPAPhoto enrPhoto : (List<JPAPhoto>) enrAlbum.getPhotoList()) {
                                    enrPhoto.setAlbum(enrAlbum);
                                    
                                    if (enrPhoto.getTagPhotoList() != null) {
                                        for (JPATagPhoto tagPhoto : (List<JPATagPhoto>) enrPhoto.getTagPhotoList()) {
                                            tagPhoto.setPhoto(enrPhoto);
                                            
                                            JPATag enrTag = tags.get(tagPhoto.tagId);
                                            tagPhoto.setTag(enrTag);
                                            if (enrTag.getTagPhotoList() == null)
                                                enrTag.setTagPhotoList(new LinkedList<TagPhoto>());
                                            enrTag.getTagPhotoList().add(tagPhoto);
                                        }
                                    }
                                    em.merge(enrPhoto);
                                }
                            }
                            em.merge(enrAlbum);
                        }
                    }

                    if (enrTheme.getTagThemeList() != null) {
                        for (JPATagTheme enrTagTheme : (List<JPATagTheme>) enrTheme.getTagThemeList()) {
                            enrTagTheme.setTheme(enrTheme);
                            
                            JPATag enrTag = tags.get(enrTagTheme.tagId);
                            enrTagTheme.setTag(enrTag);
                            if (enrTag.getTagThemeList() == null)
                                enrTag.setTagThemeList(new LinkedList<TagTheme>());
                            enrTag.getTagThemeList().add(enrTagTheme);
                        }
                    }
                    em.merge(enrTheme);
                }
            }
            
            log.warn("Second pass for the themes");
            
            if (web.Themes != null) {
                for (JPATheme enrTheme : web.Themes) {
                    log.info( "2. Theme: {}", enrTheme.getNom()) ;

                    if (enrTheme.getCarnetList() != null) {
                        for (JPACarnet enrCarnet : (List<JPACarnet>) enrTheme.getCarnetList()) {
                            log.info( "2. Carnet: {}", enrCarnet.getNom()) ;
                            
                            if (enrCarnet.pictureId != null) {
                                enrCarnet.setPicture(photos.get(enrCarnet.pictureId));
                            }
                            
                            if (enrCarnet.albumIdList != null) {
                                if (enrCarnet.getAlbumList() == null)
                                    enrCarnet.setAlbumList(new LinkedList<Album>());
                                for (Integer albumId : enrCarnet.albumIdList) {
                                    JPAAlbum enrAlbum = albums.get(albumId);
                                    
                                    enrCarnet.getAlbumList().add(enrAlbum);
                                    
                                    if (enrAlbum.getCarnetList() == null)
                                        enrAlbum.setCarnetList(new LinkedList<Carnet>());
                                    enrAlbum.getCarnetList().add(enrCarnet);
                                    em.merge(enrAlbum);
                                }
                            }

                            if (enrCarnet.photoIdList != null) {
                                if (enrCarnet.getPhotoList() == null)
                                    enrCarnet.setPhotoList(new LinkedList<Photo>());
                                
                                for (Integer photoId : enrCarnet.photoIdList) {
                                    JPAPhoto enrPhoto = photos.get(photoId);
                                    enrCarnet.getPhotoList().add(enrPhoto);
                                    
                                    if (enrPhoto.getCarnetList() == null)
                                        enrPhoto.setCarnetList(new LinkedList<Carnet>());
                                    enrPhoto.getCarnetList().add(enrCarnet);
                                    em.merge(enrPhoto);
                                }
                            }
                            
                            em.merge(enrCarnet);
                        }
                    }
                    
                    if (enrTheme.getAlbumList() != null) {
                        for (JPAAlbum enrAlbum : (List<JPAAlbum>) enrTheme.getAlbumList()) {
                            log.info( "2. Album: {}", enrAlbum.getNom()) ;
                            
                            if (enrAlbum.pictureId != null) {
                                enrAlbum.setPicture(photos.get(enrAlbum.pictureId));
                            }                    

                            if (enrAlbum.getPhotoList() != null) {
                                for (JPAPhoto enrPhoto : (List<JPAPhoto>) enrAlbum.getPhotoList()) {
                                    
                                    if (enrPhoto.tagAuthorId != null) {
                                        JPATag enrTag = tags.get(enrPhoto.tagAuthorId);
                                        enrPhoto.setTagAuthor(enrTag);
                                        if (enrTag.getAuthorList() == null)
                                            enrTag.setAuthorList(new LinkedList<Photo>());
                                        enrTag.getAuthorList().add(enrPhoto);
                                        em.merge(enrTag);
                                    }
                                    em.merge(enrPhoto);
                                }
                            }
                            em.merge(enrAlbum);
                        }
                    }

                    if (enrTheme.getTagThemeList() != null) {
                        for (JPATagTheme enrTagTheme : (List<JPATagTheme>) enrTheme.getTagThemeList()) {
                            
                            if (enrTagTheme.photoId != null) {
                                enrTagTheme.setPhoto(photos.get(enrTagTheme.photoId));
                            }
                        }
                    }
                    
                    if (enrTheme.backgroundId != null) {
                        enrTheme.setBackground(photos.get(enrTheme.backgroundId));
                    }
                    if (enrTheme.pictureId != null) {
                        enrTheme.setPicture(photos.get(enrTheme.pictureId));
                    }
                    em.merge(enrTheme);
                }
            }
            
            
            
        } catch (Exception ex) {
            throw new DatabaseFacadeLocalException(ex);
        } finally {
            //em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1;").executeUpdate();
        }
    }

    @Override
    public void truncateDb() {
        for (Object enr : themeDAO.findAll()) {
            em.remove(enr);
        }

        for (Object enr : userDAO.findAll()) {
            em.remove(enr);
        }

        for (Object enr : tagDAO.findAll()) {
            em.remove(enr);
        }
    }
}
