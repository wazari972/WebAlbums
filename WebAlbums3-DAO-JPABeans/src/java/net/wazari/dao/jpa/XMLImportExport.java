/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;
import net.wazari.common.util.XmlUtils;
import net.wazari.dao.DatabaseFacadeLocal.DatabaseFacadeLocalException;
import net.wazari.dao.*;
import net.wazari.dao.entity.*;
import net.wazari.dao.jpa.entity.*;
import net.wazari.dao.jpa.entity.xml.WebAlbumsXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @EJB
    private GeolocalisationFacadeLocal geoDAO;
    @EJB
    private PersonFacadeLocal personDAO;
    
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
            
            Map<Integer, JPATag> tags = new HashMap<Integer, JPATag>() ;
            Map<Integer, JPAPhoto> photos = new HashMap<Integer, JPAPhoto>() ;
            Map<Integer, JPAAlbum> albums = new HashMap<Integer, JPAAlbum>() ;
            Map<Integer, JPATheme> themes = new HashMap<Integer, JPATheme>() ;
            Map<Integer, JPAUtilisateur> users = new HashMap<Integer, JPAUtilisateur>() ;
            
            if (web.Utilisateurs != null) {
                for (JPAUtilisateur enrUser : web.Utilisateurs) {
                    users.put(enrUser.getId(), em.merge(enrUser)) ;
                }            
            }
            
            log.warn( "Process entities") ;
            if (web.Tags != null) {
                log.warn( "Import {} Tag",web.Tags.size()) ;
                
                for (JPATag enrTag : web.Tags) {
                    log.info( "Tag: {}", enrTag.getNom()) ;

                    Tag newTag = tagDAO.newTag();
                    newTag.setId(enrTag.getId());
                    newTag.setNom(enrTag.getNom());
                    newTag.setTagType(enrTag.getTagType());
                    newTag.setMinor(enrTag.isMinor());
                    newTag = em.merge(newTag);
                    
                    if (enrTag.getGeolocalisation() != null) {
                        JPAGeolocalisation enrGeo = enrTag.getGeolocalisation();
                        Geolocalisation newGeo = geoDAO.newGeolocalisation();
                        newGeo.setTag(newTag);
                        
                        newGeo.setLatitude(enrGeo.getLatitude());
                        newGeo.setLongitude(enrGeo.getLongitude());
                        
                        newTag.setGeolocalisation(newGeo);
                        
                        newTag = em.merge(newTag);
                    }

                    if (enrTag.getPerson() != null) {
                        Person enrPerson = enrTag.getPerson();
                        Person newPerson = personDAO.newPerson();
                        newPerson.setTag(newTag);
                        
                        newPerson.setBirthdate(enrPerson.getBirthdate());
                        newPerson.setContact(enrPerson.getContact());
                        
                        newTag.setPerson(newPerson);
                        newTag = em.merge(newTag);
                    }
                    
                    tags.put(enrTag.getId(), (JPATag) em.merge(newTag)) ;
                }
                
                for (JPATag enrTag : web.Tags) {
                    if (enrTag.parentId != null) {
                        log.info( "Tag {} with parent {}", enrTag.getNom(), enrTag.parentId) ;
                        // not the best implementation, but only solution which 
                        // works without messing with PERSONS without id ...
                        Tag currentTag = tagDAO.find(enrTag.getId());
                        Tag parentTag = tagDAO.find(enrTag.parentId);
                        currentTag.setParent(parentTag);
                        
                        em.merge(currentTag);
                    }
                }
            }
            
            if (web.Themes != null) {
                Collections.reverse(web.Themes);
                for (JPATheme enrTheme : web.Themes) {
                    log.info( "Theme: {}", enrTheme.getNom()) ;
                    
                    Theme newTheme = themeDAO.newTheme(enrTheme.getId(), enrTheme.getNom());
                    newTheme.setLatitude(enrTheme.getLatitude());
                    newTheme.setLongitude(enrTheme.getLongitude());
                    newTheme = em.merge(newTheme) ;
                    
                    if (enrTheme.getAlbumList() != null) {
                        for (JPAAlbum enrAlbum : (List<JPAAlbum>) enrTheme.getAlbumList()) {
                            Album newAlbum = albumDAO.newAlbum();
                            newAlbum.setId(enrAlbum.getId());
                            newAlbum.setTheme(newTheme);
                            newAlbum.setDescription(enrAlbum.getDescription());
                            newAlbum.setDate(enrAlbum.getDate());
                            newAlbum.setNom(enrAlbum.getNom());
                            
                            newAlbum.setDroit(users.get(enrAlbum.droitId));
                            newAlbum = em.merge(newAlbum);
                            albums.put(enrAlbum.getId(), (JPAAlbum) newAlbum);
                            
                            newAlbum.setGpxList(new LinkedList<Photo>());
                            if (enrAlbum.getGpxList() != null) {
                                for (Photo enrGpx : (List<Photo>) enrAlbum.getGpxList()) {
                                    enrGpx.setAlbum(newAlbum);
                                    newAlbum.getGpxList().add(enrGpx);
                                    em.merge(enrGpx);
                                }
                            }
                            
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
                                    newPhoto.setFocal(enrPhoto.getFocal());
                                    
                                    newPhoto =  em.merge(newPhoto);
                                    
                                    if (enrPhoto.getTagPhotoList() != null) {
                                        for (JPATagPhoto tagPhoto : (List<JPATagPhoto>) enrPhoto.getTagPhotoList()) {
                                            TagPhoto newTagPhoto = tagPhotoDAO.newTagPhoto();
                                            
                                            newTagPhoto.setPhoto(enrPhoto);
                                            
                                            JPATag enrTag = tags.get(tagPhoto.tagId);
                                            newTagPhoto.setTag(enrTag);
                                            if (enrTag.getTagPhotoList() == null) {
                                                enrTag.setTagPhotoList(new LinkedList<TagPhoto>());
                                            }
                                            enrTag.getTagPhotoList().add(newTagPhoto);
                                            
                                            em.merge(newTagPhoto);
                                        }
                                    }
                                    
                                    if (enrPhoto.tagAuthorId != null) {
                                        JPATag enrTag = tags.get(enrPhoto.tagAuthorId);
                                        newPhoto.setTagAuthor(enrTag);
                                        if (enrTag.getAuthorList() == null) {
                                            enrTag.setAuthorList(new LinkedList<Photo>());
                                        }
                                        enrTag.getAuthorList().add(newPhoto);
                                        em.merge(enrTag);
                                    }
                                    
                                    newPhoto = em.merge(newPhoto);
                                    photos.put(newPhoto.getId(), (JPAPhoto) newPhoto);
                                }
                            }
                            
                            if (enrAlbum.pictureId != null) {
                                newAlbum.setPicture(photos.get(enrAlbum.pictureId));
                            }
                            
                            em.merge(newAlbum);
                        }
                    }
                    
                    if (enrTheme.getTagThemeList() != null) {
                        for (JPATagTheme enrTagTheme : (List<JPATagTheme>) enrTheme.getTagThemeList()) {
                            TagTheme newTagTheme = tagThemeDAO.newTagTheme();
                            newTagTheme.setTheme(newTheme);
                            
                            newTagTheme.setTag(tags.get(enrTagTheme.tagId));
                            newTagTheme.setPhoto(photos.get(enrTagTheme.photoId));
                            newTagTheme.setVisible(enrTagTheme.isVisible());
                            
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
                    
                    themes.put(newTheme.getId(), (JPATheme) em.merge(newTheme));
                }
                
                for (JPATheme enrTheme : web.Themes) {
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
                            newCarnet.setTheme(themes.get(enrTheme.getId()));
                            
                            newCarnet = em.merge(newCarnet);
                            
                            if (enrCarnet.pictureId != null) {
                                newCarnet.setPicture(photos.get(enrCarnet.pictureId));
                            }
                            
                            if (enrCarnet.albumIdList != null) {
                                newCarnet.setAlbumList(new LinkedList<Album>());
                                for (Integer albumId : enrCarnet.albumIdList) {
                                    JPAAlbum enrAlbum = albums.get(albumId);
                                    newCarnet.getAlbumList().add(enrAlbum);
                                }
                            }
                            
                            if (enrCarnet.photoIdList != null) {
                                newCarnet.setPhotoList(new LinkedList<Photo>());
                                
                                for (Integer photoId : enrCarnet.photoIdList) {
                                    JPAPhoto enrPhoto = photos.get(photoId);
                                    newCarnet.getPhotoList().add(enrPhoto);
                                }
                            }
                            
                            em.merge(newCarnet);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new DatabaseFacadeLocalException(ex);
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
