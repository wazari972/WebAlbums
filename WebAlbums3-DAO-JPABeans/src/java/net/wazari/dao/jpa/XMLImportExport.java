/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
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
            log.info( "XML Saved!");
        } catch (JAXBException ex) {
            log.error("JAXBException", ex);
            throw new DatabaseFacadeLocalException(ex.getMessage());
        }
    }

    @Override
    public void importXml(String path) {
        try {
            WebAlbumsXML web = XmlUtils.reload(new FileInputStream(new File(path+FILENAME)), WebAlbumsXML.clazzez);
            if (web == null) {
                log.warn("Couldn't load the XML backup ...");
                return ;
            }
            
            Map<Integer, JPATag> tags = new HashMap<Integer, JPATag>(web.Tags.size()) ;
            Map<Integer, JPAPhoto> photos = new HashMap<Integer, JPAPhoto>() ;
            Map<Integer, JPATheme> themes = new HashMap<Integer, JPATheme>(web.Themes.size()) ;
            Map<Integer, JPAAlbum> albums = new HashMap<Integer, JPAAlbum>() ;
            Map<Integer, JPAUtilisateur> users = new HashMap<Integer, JPAUtilisateur>(web.Utilisateurs.size()) ;

            for (JPAUtilisateur enrUser : web.Utilisateurs) {
                log.info( "User: {}", enrUser) ;
                
                users.put(enrUser.getId(), em.merge(enrUser)) ;
            }            
            
            log.info( "Import {} Tag",web.Tags.size()) ;
            for (JPATag enrTag : web.Tags) {
                log.info( "Tag: {}", enrTag) ;
                
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
            
            for (JPATheme enrTheme : web.Themes) {
                log.info( "Theme: {}", enrTheme) ;
                em.merge(enrTheme);
                themes.put(enrTheme.getId(), enrTheme) ;
                
                for (JPAAlbum enrAlbum : (List<JPAAlbum>)enrTheme.getAlbumList()) {
                    albums.put(enrAlbum.getId(), enrAlbum);
                    enrAlbum.setTheme(enrTheme);
                    em.merge(enrAlbum);
                    
                    for (JPAGpx enrGpx : (List<JPAGpx>) enrAlbum.getGpxList()) {
                        enrGpx.setAlbum(enrAlbum);
                        em.merge(enrGpx);
                    }
                    
                    for (JPAPhoto enrPhoto : (List<JPAPhoto>) enrAlbum.getPhotoList()) {
                        enrPhoto.setAlbum(enrAlbum);
                        
                        if (enrPhoto.tagAuthorId != null) {
                            enrPhoto.setTagAuthor(tags.get(enrPhoto.tagAuthorId));
                            
                        }
                        
                        for (JPATagPhoto tagPhoto : (List<JPATagPhoto>) enrPhoto.getTagPhotoList()) {
                            tagPhoto.setPhoto(enrPhoto);
                            tagPhoto.setTag(tags.get(tagPhoto.tagId));
                        }
                        
                        photos.put(enrPhoto.getId(), em.merge(enrPhoto));
                    }
                    
                    
                    if (enrAlbum.pictureId != null) {
                        enrAlbum.setPicture(photos.get(enrAlbum.pictureId));
                    }
                    
                    if (enrAlbum.droitId != null) {
                        enrAlbum.setDroit(users.get(enrAlbum.droitId));
                    }
                    em.merge(enrAlbum);
                }
                
                for (JPATagTheme enrTagTheme : (List<JPATagTheme>) enrTheme.getTagThemeList()) {
                    enrTagTheme.setTheme(enrTheme);
                    enrTagTheme.setTag(tags.get(enrTagTheme.tagId));
                    
                    if (enrTagTheme.photoId != null) {
                        enrTagTheme.setPhoto(photos.get(enrTagTheme.photoId));
                    }
                }
                
                for (JPACarnet enrCarnet : (List<JPACarnet>) enrTheme.getCarnetList()) {
                    for (Integer albumId : enrCarnet.albumIdList) {
                        enrCarnet.getAlbumList().add(albums.get(albumId));
                    }

                    for (Integer photoId : enrCarnet.photoIdList) {
                        enrCarnet.getPhotoList().add(photos.get(photoId));
                    }

                    if (enrCarnet.pictureId != null) {
                        enrCarnet.setPicture(photos.get(enrCarnet.pictureId));
                    }

                    if (enrCarnet.droitId != null) {
                        enrCarnet.setDroit(users.get(enrCarnet.droitId));
                    }

                    em.merge(enrCarnet);
                }
                
                if (enrTheme.backgroundId != null) {
                    enrTheme.setBackground(photos.get(enrTheme.backgroundId));
                }
                if (enrTheme.pictureId != null) {
                    enrTheme.setPicture(photos.get(enrTheme.pictureId));
                }
                
            }
            
            /************************************/
            for (JPATag enrTag : web.Tags) {
                if (enrTag.parentId != null) {
                    enrTag.setParent(tags.get(enrTag.parentId));
                    em.merge(enrTag);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getClass().toString(), ex);
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
