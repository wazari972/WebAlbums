/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.Collection;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionPhoto.*;
import net.wazari.common.util.XmlBuilder;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Tag;
import net.wazari.service.exchange.ViewSession;

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
    XmlBuilder displayPhoto(PhotoRequest rq, ViewSessionPhotoDisplay vSession, XmlBuilder thisPage, XmlBuilder submit) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatPhotoEDIT(ViewSessionPhotoEdit vSession, XmlBuilder submit) throws WebAlbumsServiceException;
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatPhotoDISPLAY(ViewSessionPhotoDisplay vSession, XmlBuilder submit) throws WebAlbumsServiceException ;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession,Boolean correct) throws WebAlbumsServiceException ;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatRANDOM(ViewSession vSession) throws WebAlbumsServiceException ;
}
