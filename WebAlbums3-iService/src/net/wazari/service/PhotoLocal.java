/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.xml.photo.XmlPhotoRandom;
import net.wazari.service.exchange.xml.photo.XmlPhotoEdit;
import net.wazari.service.exchange.xml.photo.XmlPhotoSubmit;
import net.wazari.service.exchange.xml.photo.XmlPhotoDisplay;
import java.util.Collection;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Tag;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.xml.common.XmlFrom;
import net.wazari.service.exchange.xml.photo.XmlPhotoAbout;
import net.wazari.service.exchange.xml.photo.XmlPhotoList;
import net.wazari.service.exchange.xml.photo.XmlPhotoFastEdit;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE, UserLocal.MANAGER_ROLE})
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
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlPhotoList displayPhoto(PhotoRequest rq, ViewSessionPhotoDisplay vSession, XmlPhotoSubmit submit, XmlFrom fromPage) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlPhotoEdit treatPhotoEDIT(ViewSessionPhotoEdit vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException;
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlPhotoDisplay treatPhotoDISPLAY(ViewSessionPhotoDisplay vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException ;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlPhotoSubmit treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession,Boolean correct) throws WebAlbumsServiceException ;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlPhotoRandom treatRANDOM(ViewSession vSession) throws WebAlbumsServiceException ;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlPhotoAbout treatABOUT(ViewSessionPhoto vSession) throws WebAlbumsServiceException ;
    
    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlPhotoFastEdit treatFASTEDIT(ViewSessionPhotoFastEdit vSession) throws WebAlbumsServiceException ;
}

