/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.Collection;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Tag;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSimple;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.service.exchange.xml.common.XmlFrom;
import net.wazari.service.exchange.xml.photo.*;

/**
 *
 * @author kevin
 */
@Local
public interface PhotoLocal {
    
    enum TypeRequest {
        PHOTO, TAG
    }
    class PhotoRequest {

        public PhotoRequest(TypeRequest type, Album albumId) {
            this.type = type;
            this.albumId = albumId ;
            this.listTagId = null ;
        }
        public PhotoRequest(TypeRequest type, Collection<Tag> listTagId) {
            this.type = type;
            this.listTagId = listTagId ;
            this.albumId = null ;
        }
        public Album albumId ;
        public TypeRequest type ;
        public Collection<Tag> listTagId ;
    }
    
    XmlPhotoList displayPhoto(PhotoRequest rq, ViewSessionPhotoDisplay vSession, XmlPhotoSubmit submit, XmlFrom fromPage) throws WebAlbumsServiceException;

    XmlPhotoEdit treatPhotoEDIT(ViewSessionPhotoEdit vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException;
    
    XmlPhotoDisplay treatPhotoDISPLAY(ViewSessionPhotoDisplay vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException ;

    XmlPhotoSubmit treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession,Boolean correct) throws WebAlbumsServiceException ;

    XmlPhotoRandom treatRANDOM(ViewSession vSession) throws WebAlbumsServiceException ;

    XmlPhotoAbout treatABOUT(ViewSessionPhotoSimple vSession) throws WebAlbumsServiceException ;
    
    XmlPhotoFastEdit treatFASTEDIT(ViewSessionPhotoFastEdit vSession) throws WebAlbumsServiceException ;
    
    XmlDetails getPhotoByPath(ViewSession vSession, String path) throws WebAlbumsServiceException;
}

