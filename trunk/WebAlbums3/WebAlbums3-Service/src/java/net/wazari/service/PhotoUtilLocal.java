/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.dao.entity.Photo;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
public interface PhotoUtilLocal {

    void addTags(Photo p, Integer[] tags) throws WebAlbumsServiceException;
    void setTags(Photo p, Integer[] tags) throws WebAlbumsServiceException;
    void updateDroit(Photo p, Integer droit);
    void removeExtraTags(Photo p, Integer[] tags) throws WebAlbumsServiceException;
    void removeTag(Photo p, int tag) throws WebAlbumsServiceException;

    String getExtention(ViewSession vSession, Photo p);

    String getImagePath(ViewSession vSession, Photo p);
    String getMiniPath(ViewSession vSession, Photo p);
    String getThemedPath(Photo p);

    int getWidth(ViewSession vSession, Photo p, boolean large) ;
    int getHeight(ViewSession vSession, Photo p, boolean large);

    void retreiveExif(ViewSession vSession, Photo p);

    boolean rotate(ViewSession vSession, Photo p, String degrees) throws WebAlbumsServiceException;

    XmlBuilder getXmlExif(Photo p) ;


}
