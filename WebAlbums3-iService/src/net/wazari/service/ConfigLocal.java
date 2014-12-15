/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exchange.xml.config.*;

/**
 *
 * @author kevin
 */
@Local
public interface ConfigLocal {
    XmlConfigDelTag treatDELTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlConfigImport treatIMPORT(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlConfigSetHome treatSETHOME(ViewSessionConfig vSession) throws WebAlbumsServiceException;
    
    XmlConfigModGeo treatMODGEO(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlConfigModTag treatMODTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlConfigModVis treatMODVIS(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlConfigNewTag treatNEWTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlConfigDelTheme treatDELTHEME(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlConfigLinkTag treatLINKTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    XmlConfigModPers treatMODPERS(ViewSessionConfig vSession) throws WebAlbumsServiceException;
    
    XmlConfigModMinor treatMODMINOR(ViewSessionConfig vSession) throws WebAlbumsServiceException;
}
