/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import javax.ejb.EJB;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.WebPageLocal;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.album.XmlGpx;
import net.wazari.service.exchange.xml.carnet.XmlCarnet;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.service.exchange.xml.common.XmlPhotoAlbumUser;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.service.exchange.xml.photo.XmlPhoto;
import net.wazari.service.exchange.xml.photo.XmlPhotoId;

/**
 *
 * @author kevin
 */
public class DaoToXmlBean {
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private WebPageLocal webPageService;
    @EJB
    PhotoUtil photoUtil;
   
    public void convertPhotoDetails(ViewSession vSession, Photo enrPhoto, 
            XmlDetails details, boolean withAlbumDetails) throws WebAlbumsServiceException {
        details.photoId = new XmlPhotoId(enrPhoto.getId());
        if (vSession.directFileAccess()) {
            details.photoId.path = enrPhoto.getPath(true) ;
        }

        if (enrPhoto.isGpx()) {
            //keep null if false
            details.isGpx =  true; 
        }

        details.setDescription(enrPhoto.getDescription());

        //tags de cette photo
        details.tag_used = webPageService.displayListIBTD(ViewSession.Mode.TAG_USED, vSession, enrPhoto,
                ViewSession.Box.NONE, enrPhoto.getAlbum().getDate());

        details.albumId = enrPhoto.getAlbum().getId();
        
        if (withAlbumDetails) {
            details.albumName = enrPhoto.getAlbum().getNom();
            details.albumDate = enrPhoto.getAlbum().getDate();
        }

        details.stars = enrPhoto.getStars();
    }
    
    public void addUserOutside(ViewSession vSession, Photo enrPhoto, 
            XmlDetails details) {
        String name;
        boolean outside;
        Utilisateur enrUser = userDAO.find(enrPhoto.getDroit());
        if (enrUser != null) {
            name = enrUser.getNom();
            outside = false;
        } else {
            name = userDAO.loadUserOutside(enrPhoto.getAlbum().getId()).getNom();
            outside = true;
        }

        details.user = new XmlPhotoAlbumUser(name, outside);
    }
    
    public void addAuthorDetails(ViewSession vSession, Photo enrPhoto, XmlPhoto photo) {
        if (enrPhoto.getTagAuthor() != null) {
                Tag enrAuthor = enrPhoto.getTagAuthor();
                photo.author = new XmlWebAlbumsList.XmlWebAlbumsTagWho();
                photo.author.name = enrAuthor.getNom();
                photo.author.id = enrAuthor.getId();
                if (enrAuthor.getPerson() != null) {
                    photo.author.contact = enrAuthor.getPerson().getContact();
                }
            }
    }

    void addExifDetails(ViewSessionPhotoDisplay vSession, Photo enrPhoto, XmlPhoto photo) {
        photo.exif = photoUtil.getXmlExif(enrPhoto) ;
    }

    void convertAlbum(ViewSession vSession, Album enrAlbum, XmlAlbum album) throws WebAlbumsServiceException {
        album.id = enrAlbum.getId();
        album.title = enrAlbum.getNom();
        album.droit = enrAlbum.getDroit().getNom();
        album.date = webPageService.xmlDate(enrAlbum.getDate());
        
        //tags de l'album
        album.details.tag_used = webPageService.displayListIBTD(ViewSession.Mode.TAG_GEO, 
                            vSession, enrAlbum, ViewSession.Box.NONE, enrAlbum.getDate());
        album.details.setDescription(enrAlbum.getDescription());
        
        if (enrAlbum.getPicture() != null) {
            album.details.photoId = new XmlPhotoId(enrAlbum.getPicture().getId()) ;
            if (vSession.directFileAccess()) {
                album.details.photoId.path = enrAlbum.getPicture().getPath(true) ;
            }
        }
    }

    void convertCarnet(ViewSession vSession, Carnet enrCarnet, XmlCarnet carnet) {
        carnet.date = webPageService.xmlDate(enrCarnet.getDate());
        carnet.id = enrCarnet.getId();
        carnet.name = enrCarnet.getNom();
        if (enrCarnet.getPicture() != null) {
            carnet.picture = new XmlPhotoId(enrCarnet.getPicture().getId());
            if (vSession.directFileAccess()) {
                carnet.picture.path = enrCarnet.getPicture().getPath(true);
            }
        }
    }

    void convertGpx(ViewSessionPhotoDisplay vSession, Photo enrGpx, XmlGpx gpx) {
        gpx.id = enrGpx.getId();
        if (vSession.directFileAccess()) {
            gpx.path = enrGpx.getPath(true);
        }
        gpx.setDescription(enrGpx.getDescription());
    }
}
