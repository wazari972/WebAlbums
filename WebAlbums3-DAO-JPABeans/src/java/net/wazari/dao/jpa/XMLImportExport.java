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
                        em.merge(enrTag.getGeolocalisation());
                    }

                    if (enrTag.getPerson() != null) {
                        enrTag.getPerson().setTag(enrTag);
                        em.merge(enrTag.getPerson());
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
                    
                    Theme newTheme = themeDAO.newTheme(enrTheme.getId(), enrTheme.getNom());
                    newTheme = em.merge(newTheme) ;
                    
                    if (enrTheme.getAlbumList() != null) {
                        for (JPAAlbum enrAlbum : (List<JPAAlbum>) enrTheme.getAlbumList()) {
                            log.info( "Album: {}", enrAlbum.getNom()) ;

                            Album newAlbum = albumDAO.newAlbum();
                            newAlbum.setTheme(newTheme);
                            newAlbum.setDescription(enrAlbum.getDescription());
                            newAlbum.setDate(enrAlbum.getDate());
                            newAlbum.setNom(enrAlbum.getNom());
                            
                            newAlbum.setGpxList(new LinkedList<Gpx>());
                            if (enrAlbum.getGpxList() != null) {
                                for (JPAGpx enrGpx : (List<JPAGpx>) enrAlbum.getGpxList()) {
                                    enrGpx.setAlbum(enrAlbum);
                                    newAlbum.getGpxList().add(enrGpx);
                                    em.merge(enrGpx);
                                }
                            }
                            
                            newAlbum.setDroit(users.get(enrAlbum.droitId));
                            
                            newAlbum = em.merge(newAlbum);
                            
                            albums.put(enrAlbum.getId(), (JPAAlbum) newAlbum);
                            
                            if (enrAlbum.getPhotoList() != null) {
                                for (JPAPhoto enrPhoto : (List<JPAPhoto>) enrAlbum.getPhotoList()) {
                                    Photo newPhoto = photoDAO.newPhoto();
                                    
                                    newPhoto.setId(enrPhoto.getId());
                                    newPhoto.setAlbum(newAlbum);
                                    newPhoto.setDroit(enrPhoto.getDroit());
                                    newPhoto.setDescription(enrPhoto.getDescription());
                                    newPhoto.setPath(enrPhoto.getPath());
                                    newPhoto.setType(enrPhoto.getType());
                                    newPhoto.setStars(enrPhoto.getStars());
                                    
                                    newPhoto.setModel(enrPhoto.getModel());
                                    newPhoto.setDate(enrPhoto.getDate());
                                    newPhoto.setIso(enrPhoto.getIso());
                                    newPhoto.setExposure(enrPhoto.getExposure());
                                    newPhoto.setHeight(enrPhoto.getHeight());
                                    newPhoto.setWidth(enrPhoto.getWidth());
                                    newPhoto.setFlash(enrPhoto.getFlash());

                                    photos.put(newPhoto.getId(), (JPAPhoto) em.merge(newPhoto));
                                    
                                    if (enrPhoto.getTagPhotoList() != null) {
                                        for (JPATagPhoto tagPhoto : (List<JPATagPhoto>) enrPhoto.getTagPhotoList()) {
                                            TagPhoto newTagPhoto = tagPhotoDAO.newTagPhoto();
                                            
                                            newTagPhoto.setPhoto(enrPhoto);
                                            
                                            JPATag enrTag = tags.get(tagPhoto.tagId);
                                            newTagPhoto.setTag(enrTag);
                                            if (enrTag.getTagPhotoList() == null)
                                                enrTag.setTagPhotoList(new LinkedList<TagPhoto>());
                                            enrTag.getTagPhotoList().add(newTagPhoto);
                                            
                                            em.merge(newTagPhoto);
                                        }
                                    }
                                    
                                    if (enrPhoto.tagAuthorId != null) {
                                        JPATag enrTag = tags.get(enrPhoto.tagAuthorId);
                                        newPhoto.setTagAuthor(enrTag);
                                        if (enrTag.getAuthorList() == null)
                                            enrTag.setAuthorList(new LinkedList<Photo>());
                                        enrTag.getAuthorList().add(newPhoto);
                                        em.merge(enrTag);
                                    }
                                    
                                    em.merge(newPhoto);
                                }
                            }
                            
                            if (enrAlbum.pictureId != null) {
                                newAlbum.setPicture(photos.get(enrAlbum.pictureId));
                            }
                            
                            em.merge(newAlbum);
                        }
                    }

                    if (enrTheme.getCarnetList() != null) {
                        for (JPACarnet enrCarnet : (List<JPACarnet>) enrTheme.getCarnetList()) {
                            log.info( "Carnet: {}", enrCarnet.getNom()) ;
                            
                            Carnet newCarnet = carnetDAO.newCarnet();
                            newCarnet.setId(enrCarnet.getId());
                            newCarnet.setDroit(users.get(enrCarnet.droitId));
                            newCarnet.setDescription(enrCarnet.getDescription());
                            newCarnet.setText(enrCarnet.getText());
                            newCarnet.setDate(enrCarnet.getDate());
                            newCarnet.setNom(enrCarnet.getNom());
                            newCarnet.setTheme(newTheme);
                            
                            if (enrCarnet.pictureId != null) {
                                newCarnet.setPicture(photos.get(enrCarnet.pictureId));
                            }
                            
                            if (enrCarnet.albumIdList != null) {
                                newCarnet.setAlbumList(new LinkedList<Album>());
                                for (Integer albumId : enrCarnet.albumIdList) {
                                    JPAAlbum enrAlbum = albums.get(albumId);
                                    
                                    newCarnet.getAlbumList().add(enrAlbum);
                                    
                                    if (enrAlbum.getCarnetList() == null)
                                        enrAlbum.setCarnetList(new LinkedList<Carnet>());
                                    
                                    enrAlbum.getCarnetList().add(enrCarnet);
                                    em.merge(enrAlbum);
                                }
                            }

                            if (enrCarnet.photoIdList != null) {
                                newCarnet.setPhotoList(new LinkedList<Photo>());
                                
                                for (Integer photoId : enrCarnet.photoIdList) {
                                    JPAPhoto enrPhoto = photos.get(photoId);
                                    newCarnet.getPhotoList().add(enrPhoto);
                                    
                                    if (enrPhoto.getCarnetList() == null)
                                        enrPhoto.setCarnetList(new LinkedList<Carnet>());
                                    enrPhoto.getCarnetList().add(enrCarnet);
                                    em.merge(enrPhoto);
                                }
                            }
                            
                            em.merge(newCarnet);
                        }
                    }
                    
                    if (enrTheme.getTagThemeList() != null) {
                        for (JPATagTheme enrTagTheme : (List<JPATagTheme>) enrTheme.getTagThemeList()) {
                            TagTheme newTagTheme = tagThemeDAO.newTagTheme();
                            newTagTheme.setTheme(newTheme);
                            
                            newTagTheme.setTag(tags.get(enrTagTheme.tagId));
                            newTagTheme.setPhoto(photos.get(enrTagTheme.photoId));
                            
                            if (enrTagTheme.photoId != null) {
                                newTagTheme.setPhoto(photos.get(enrTagTheme.photoId));
                            }
                            
                            em.merge(newTagTheme);
                            
                        }
                    }
                    
                    if (enrTheme.backgroundId != null) {
                        newTheme.setBackground(photos.get(enrTheme.backgroundId));
                    }
                    if (enrTheme.pictureId != null) {
                        newTheme.setPicture(photos.get(enrTheme.pictureId));
                    }
                    
                    em.merge(newTheme);
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
