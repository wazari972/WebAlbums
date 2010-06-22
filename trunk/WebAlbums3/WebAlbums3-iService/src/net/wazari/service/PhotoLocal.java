/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.*;
import net.wazari.common.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
@RolesAllowed({UserLocal.VIEWER_ROLE, UserLocal.ADMIN_ROLE})
public interface PhotoLocal {
    enum TypeRequest {
        PHOTO, TAG
    }
    class PhotoRequest {

        public PhotoRequest(TypeRequest type, Integer albumId) {
            this.type = type;
            this.albumId = albumId ;
            this.listTagId = null ;
        }
        public PhotoRequest(TypeRequest type, List<Integer> listTagId) {
            this.type = type;
            this.listTagId = listTagId ;
            this.albumId = null ;
        }
        public Integer albumId ;
        public TypeRequest type ;
        public List<Integer> listTagId ;
    }
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayPhoto(PhotoRequest rq, ViewSessionPhotoDisplay vSession, XmlBuilder thisPage, XmlBuilder submit) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatPHOTO(ViewSessionPhoto vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.ADMIN_ROLE)
    XmlBuilder treatPhotoEDIT(ViewSessionPhotoEdit vSession, XmlBuilder submit) throws WebAlbumsServiceException;
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatPhotoDISPLAY(ViewSessionPhotoDisplay vSession, XmlBuilder submit) throws WebAlbumsServiceException ;

    @RolesAllowed(UserLocal.ADMIN_ROLE)
    XmlBuilder treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession,Boolean correct) throws WebAlbumsServiceException ;

}
