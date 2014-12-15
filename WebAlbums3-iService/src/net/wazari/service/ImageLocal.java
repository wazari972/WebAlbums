/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.xml.XmlImage;

/**
 *
 * @author kevin
 */
@Local
public interface ImageLocal {    
    XmlImage treatIMG(ViewSessionImages vSession) throws WebAlbumsServiceException;

    String treatSHRINK(ViewSessionImages vSession) throws WebAlbumsServiceException;
}
