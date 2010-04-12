/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
public interface ConfigLocal {

    XmlBuilder displayCONFIG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlBuilder treatCONFIG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlBuilder treatDELTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlBuilder treatIMPORT(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlBuilder treatMODGEO(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlBuilder treatMODTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlBuilder treatMODVIS(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlBuilder treatNEWTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

}
