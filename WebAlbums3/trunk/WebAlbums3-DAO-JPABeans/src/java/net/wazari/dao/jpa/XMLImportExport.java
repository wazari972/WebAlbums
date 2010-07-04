/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.jpa.entity.JPAAlbum;
import net.wazari.dao.jpa.entity.JPAGeolocalisation;
import net.wazari.dao.jpa.entity.JPAPhoto;
import net.wazari.dao.jpa.entity.JPATag;
import net.wazari.dao.jpa.entity.JPATagPhoto;
import net.wazari.dao.jpa.entity.JPATagTheme;
import net.wazari.dao.jpa.entity.JPATheme;
import net.wazari.dao.jpa.entity.JPAUtilisateur;
/**
 *
 * @author kevinpouget
 */
@Stateless
public class XMLImportExport implements ImportExporter {
    private static final Logger log = Logger.getLogger(XMLImportExport.class.getName());

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
    
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;
    
    public void exportXml(String path) {
        WebAlbumsXML web = new WebAlbumsXML(themeDAO.findAll(), userDAO.findAll(), albumDAO.findAll(),
                photoDAO.findAll(), tagDAO.findAll(), tagThemeDAO.findAll(), tagPhotoDAO.findAll()) ;
        try {
            XmlUtils.save(new File(path+"WebAlbums.xml"), WebAlbumsXML.class, web);
            log.log(Level.INFO, "XML Saved!");
        } catch (JAXBException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    public void importXml(String path) {
        try {
            WebAlbumsXML web = XmlUtils.reload(new File(path+"WebAlbums.xml"), WebAlbumsXML.class);
            if (web == null) {
                log.warning("Couldn't load the XML backup ...");
                return ;
            }

            Map<Integer, JPATag> tags = new HashMap<Integer, JPATag>(web.getTags().size()) ;
            Map<Integer, JPAPhoto> photos = new HashMap<Integer, JPAPhoto>(web.getPhotos().size()) ;
            Map<Integer, JPATheme> themes = new HashMap<Integer, JPATheme>(web.getThemes().size()) ;
            Map<Integer, JPAAlbum> albums = new HashMap<Integer, JPAAlbum>(web.getAlbums().size()) ;
            Map<Integer, JPAUtilisateur> users = new HashMap<Integer, JPAUtilisateur>(web.getUtilisateurs().size()) ;

            for (JPATheme enrTheme : web.getThemes()) {
                log.log(Level.INFO, "Theme: {0}", enrTheme) ;
                em.merge(enrTheme);
                themes.put(enrTheme.getId(), enrTheme) ;
            }
            
            for (JPAUtilisateur enrUser : web.getUtilisateurs()) {
                log.log(Level.INFO, "User: {0}", enrUser) ;
                em.merge(enrUser);
                users.put(enrUser.getId(), enrUser) ;
            }

            log.log(Level.INFO, "Import {0} Album",web.getAlbums().size()) ;
            for (JPAAlbum enrAlbum : web.getAlbums()) {
                log.log(Level.FINE, "Album: {0}", enrAlbum) ;

                //Theme enrTheme = themeDAO.find(enrAlbum.getThemeId()) ;
                //enrAlbum.setTheme(enrTheme) ;
                enrAlbum.setTheme(themes.get(enrAlbum.getThemeId())) ;

                //Utilisateur enrUser = userDAO.find(enrAlbum.getDroitId()) ;
                //enrAlbum.setDroit(enrUser) ;
                enrAlbum.setDroit(users.get(enrAlbum.getDroitId())) ;

                em.merge(enrAlbum);
                albums.put(enrAlbum.getId(), enrAlbum) ;
            }

            log.log(Level.INFO, "Import {0} Photo",web.getPhotos().size()) ;
            for (JPAPhoto enrPhoto : web.getPhotos()) {
                log.log(Level.FINE, "Photo: {0} ({1})", new Object[]{enrPhoto, enrPhoto.getAlbumId()}) ;

                //Album enrAlbum = albumDAO.find(enrPhoto.getAlbumId()) ;
                //enrPhoto.setAlbum(enrAlbum) ;
                enrPhoto.setAlbum(albums.get(enrPhoto.getAlbumId())) ;

                em.merge(enrPhoto);
                photos.put(enrPhoto.getId(), enrPhoto) ;
            }

            log.log(Level.INFO, "Import {0} Tag",web.getTags().size()) ;
            for (JPATag enrTag : web.getTags()) {
                log.log(Level.FINE, "Tag: {0}", enrTag) ;

                JPAGeolocalisation enrGeo = enrTag.getGeolocalisation() ;
                if (enrGeo != null) {
                    enrGeo.setTag1(enrTag);
                    log.log(Level.FINE, "\tGeo: {0}", enrGeo) ;
                }
                em.merge(enrTag);
                tags.put(enrTag.getId(), enrTag) ;
            }

            log.log(Level.INFO, "Import {0} TagPhoto",web.getTagPhoto().size()) ;
            for (JPATagPhoto enrTagPhoto : web.getTagPhoto()) {
                log.log(Level.FINE, "TagPhoto: {0}", enrTagPhoto) ;

                //Tag enrTag = tagDAO.find(enrTagPhoto.getTagId()) ;
                //enrTagPhoto.setTag(enrTag) ;
                enrTagPhoto.setTag(tags.get(enrTagPhoto.getTagId())) ;

                //Photo enrPhoto = photoDAO.find(enrTagPhoto.getPhotoId()) ;
                //enrTagPhoto.setPhoto(enrPhoto) ;
                enrTagPhoto.setPhoto(photos.get(enrTagPhoto.getPhotoId())) ;

                em.merge(enrTagPhoto);
            }

            log.log(Level.INFO, "Import {0} TagThemes",web.getTagThemes().size()) ;
            for (JPATagTheme enrTagTheme : web.getTagThemes()) {
                log.log(Level.FINE, "TagTheme: {0}", enrTagTheme) ;

                //Tag enrTag = tagDAO.find(enrTagTheme.getTagId()) ;
                //enrTagTheme.setTag(enrTag) ;
                enrTagTheme.setTag(tags.get(enrTagTheme.getTagId())) ;

                //Theme enrTheme = themeDAO.find(enrTagTheme.getThemeId()) ;
                //enrTagTheme.setTheme(enrTheme) ;
                enrTagTheme.setTheme(themes.get(enrTagTheme.getThemeId())) ;

                em.merge(enrTagTheme);
            }
        } catch (JAXBException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
    
    public void truncateDb() {
        for (Object enr : themeDAO.findAll()) {
            em.remove(enr);
        }

        for (Object enr : userDAO.findAll()) {
            em.remove(enr);
        }
    }
}
