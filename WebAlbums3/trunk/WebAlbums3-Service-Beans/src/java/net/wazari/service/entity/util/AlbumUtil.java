package net.wazari.service.entity.util;

import java.text.SimpleDateFormat;

import net.wazari.dao.entity.Photo;

import java.text.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.exception.WebAlbumsServiceException;

/**
 * This is the object class that relates to the Album table.
 * Any customizations belong here.
 */
@Stateless
public class AlbumUtil {

    @EJB
    AlbumFacadeLocal albumDAO;
    @EJB
    UtilisateurFacadeLocal userDAO;
    @EJB
    PhotoFacadeLocal photoDAO;
    @EJB
    PhotoUtil photoUtil;
    private static final Logger log = LoggerFactory.getLogger(AlbumUtil.class.toString());

    public void setTagsToPhoto(Album enrAlbum, Integer[] tags, Boolean force) throws WebAlbumsServiceException {

        for (Photo enrPhoto : enrAlbum.getPhotoList()) {
            log.info( "apply tags to {}", enrPhoto);
            if (force) {

                photoUtil.setTags(enrPhoto, tags);
            } else {
                photoUtil.addTags(enrPhoto, tags);
            }
        }

    }

    public void setDateStr(Album a, String date) throws WebAlbumsServiceException{
        if (date != null) {
            try {
                //verification
                new SimpleDateFormat("yyyy-MM-dd").parse(date);
                a.setDate(date);
            } catch (ParseException e) {
            }
        }
    }

    public void setNom(Album a, String nom) throws WebAlbumsServiceException {
        if (nom == null) {
            return;
        }

        a.setNom(nom);
    }

    public void updateDroit(Album a, Integer droit) throws WebAlbumsServiceException {
        Utilisateur enrDroit = userDAO.find(droit);
        if (enrDroit == null) {
            return;
        }
        if (enrDroit.equals(a.getDroit())) {
            return;
        }

        a.setDroit(enrDroit);
        for (Photo enrPhoto : a.getPhotoList()) {
            enrPhoto.setDroit(null);
        }
    }
}
