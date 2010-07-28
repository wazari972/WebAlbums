/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
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
    
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    public void exportXml(String path) {
        WebAlbumsXML web = new WebAlbumsXML(themeDAO.findAll(), userDAO.findAll(), albumDAO.findAll(),
                photoDAO.findAll(), tagDAO.findAll(), tagThemeDAO.findAll(), tagPhotoDAO.findAll()) ;
        try {
            XmlUtils.save(new File(path+FILENAME), web, WebAlbumsXML.clazzez);
            log.info( "XML Saved!");
        } catch (JAXBException ex) {
            log.error("JAXBException", ex);
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

            Map<Integer, JPATag> tags = new HashMap<Integer, JPATag>(web.getTags().size()) ;
            Map<Integer, JPAPhoto> photos = new HashMap<Integer, JPAPhoto>(web.getPhotos().size()) ;
            Map<Integer, JPATheme> themes = new HashMap<Integer, JPATheme>(web.getThemes().size()) ;
            Map<Integer, JPAAlbum> albums = new HashMap<Integer, JPAAlbum>(web.getAlbums().size()) ;
            Map<Integer, JPAUtilisateur> users = new HashMap<Integer, JPAUtilisateur>(web.getUtilisateurs().size()) ;

            for (JPATheme enrTheme : web.getThemes()) {
                log.info( "Theme: {}", enrTheme) ;
                em.merge(enrTheme);
                themes.put(enrTheme.getId(), enrTheme) ;
            }
            
            for (JPAUtilisateur enrUser : web.getUtilisateurs()) {
                log.info( "User: {}", enrUser) ;
                em.merge(enrUser);
                users.put(enrUser.getId(), enrUser) ;
            }

            log.info( "Import {} Album",web.getAlbums().size()) ;
            for (JPAAlbum enrAlbum : web.getAlbums()) {
                log.trace( "Album: {}", enrAlbum) ;

                //Theme enrTheme = themeDAO.find(enrAlbum.getThemeId()) ;
                //enrAlbum.setTheme(enrTheme) ;
                enrAlbum.setTheme(themes.get(enrAlbum.getThemeId())) ;

                //Utilisateur enrUser = userDAO.find(enrAlbum.getDroitId()) ;
                //enrAlbum.setDroit(enrUser) ;
                enrAlbum.setDroit(users.get(enrAlbum.getDroitId())) ;

                em.merge(enrAlbum);
                albums.put(enrAlbum.getId(), enrAlbum) ;
            }

            log.info( "Import {} Photo",web.getPhotos().size()) ;
            for (JPAPhoto enrPhoto : web.getPhotos()) {
                log.trace( "Photo: {} ({})", new Object[]{enrPhoto, enrPhoto.getAlbumId()}) ;

                //Album enrAlbum = albumDAO.find(enrPhoto.getAlbumId()) ;
                //enrPhoto.setAlbum(enrAlbum) ;
                enrPhoto.setAlbum(albums.get(enrPhoto.getAlbumId())) ;

                em.merge(enrPhoto);
                photos.put(enrPhoto.getId(), enrPhoto) ;
            }

            log.info( "Import {} Tag",web.getTags().size()) ;
            for (JPATag enrTag : web.getTags()) {
                log.trace( "Tag: {}", enrTag) ;

                JPAGeolocalisation enrGeo = enrTag.getGeolocalisation() ;
                if (enrGeo != null) {
                    enrGeo.setTag1(enrTag);
                    log.trace( "\tGeo: {}", enrGeo) ;
                }
                em.merge(enrTag);
                tags.put(enrTag.getId(), enrTag) ;
            }

            log.info( "Import {} TagPhoto",web.getTagPhoto().size()) ;
            for (JPATagPhoto enrTagPhoto : web.getTagPhoto()) {
                log.trace( "TagPhoto: {}", enrTagPhoto) ;

                //Tag enrTag = tagDAO.find(enrTagPhoto.getTagId()) ;
                //enrTagPhoto.setTag(enrTag) ;
                enrTagPhoto.setTag(tags.get(enrTagPhoto.getTagId())) ;

                //Photo enrPhoto = photoDAO.find(enrTagPhoto.getPhotoId()) ;
                //enrTagPhoto.setPhoto(enrPhoto) ;
                enrTagPhoto.setPhoto(photos.get(enrTagPhoto.getPhotoId())) ;

                em.merge(enrTagPhoto);
            }

            log.info( "Import {} TagThemes",web.getTagThemes().size()) ;
            for (JPATagTheme enrTagTheme : web.getTagThemes()) {
                log.trace( "TagTheme: {}", enrTagTheme) ;

                //Tag enrTag = tagDAO.find(enrTagTheme.getTagId()) ;
                //enrTagTheme.setTag(enrTag) ;
                enrTagTheme.setTag(tags.get(enrTagTheme.getTagId())) ;

                //Theme enrTheme = themeDAO.find(enrTagTheme.getThemeId()) ;
                //enrTagTheme.setTheme(enrTheme) ;
                enrTagTheme.setTheme(themes.get(enrTagTheme.getThemeId())) ;

                em.merge(enrTagTheme);
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
