/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.*;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
public interface PhotoLocal {
    public enum TypeRequest {
        PHOTO, TAG
    }
    public class PhotoRequest {

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
    @SuppressWarnings(value = "unchecked")
    XmlBuilder displayPhoto(PhotoRequest rq, ViewSessionPhotoDisplay vSession, XmlBuilder thisPage, XmlBuilder submit) throws WebAlbumsServiceException;

    XmlBuilder treatPHOTO(ViewSessionPhoto vSession) throws WebAlbumsServiceException;

    XmlBuilder treatPhotoEDIT(ViewSessionPhotoEdit vSession, XmlBuilder submit) throws WebAlbumsServiceException;
    XmlBuilder treatPhotoDISPLAY(ViewSessionPhotoDisplay vSession, XmlBuilder submit) throws WebAlbumsServiceException ;
    XmlBuilder treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession,Boolean correct) throws WebAlbumsServiceException ;

}
