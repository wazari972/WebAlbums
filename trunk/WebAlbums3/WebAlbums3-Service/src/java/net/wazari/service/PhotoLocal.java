/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Photo;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
public interface PhotoLocal {

    @SuppressWarnings(value = "unchecked")
    XmlBuilder displayPhoto(List<Photo> lstPhoto, ViewSessionPhoto vSession, XmlBuilder thisPage, XmlBuilder submit) throws WebAlbumsServiceException;

    XmlBuilder treatPHOTO(ViewSessionPhoto vSession) throws WebAlbumsServiceException;

    XmlBuilder treatPhotoEDIT(ViewSessionPhoto vSession, XmlBuilder submit) throws WebAlbumsServiceException;
    XmlBuilder treatPhotoDISPLAY(ViewSessionPhoto vSession, XmlBuilder submit) throws WebAlbumsServiceException ;
    XmlBuilder treatPhotoSUBMIT(ViewSessionPhoto vSession,Boolean correct) throws WebAlbumsServiceException ;

}
