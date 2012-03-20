/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import net.wazari.service.exchange.xml.config.XmlConfigDelTag;
import net.wazari.service.exchange.xml.config.XmlConfigImport;
import net.wazari.service.exchange.xml.config.XmlConfigDelTheme;
import net.wazari.service.exchange.xml.config.XmlConfigNewTag;
import net.wazari.service.exchange.xml.config.XmlConfigModVis;
import net.wazari.service.exchange.xml.config.XmlConfigLinkTag;
import net.wazari.service.exchange.xml.config.XmlConfigModGeo;
import net.wazari.service.exchange.xml.config.XmlConfigModTag;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exchange.xml.config.XmlConfigModMinor;
import net.wazari.service.exchange.xml.config.XmlConfigModPers;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.MANAGER_ROLE})
public interface ConfigLocal {
    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigDelTag treatDELTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigImport treatIMPORT(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigModGeo treatMODGEO(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigModTag treatMODTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigModVis treatMODVIS(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigNewTag treatNEWTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigDelTheme treatDELTHEME(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigLinkTag treatLINKTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigModPers treatMODPERS(ViewSessionConfig vSession) throws WebAlbumsServiceException;
    
    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlConfigModMinor treatMODMINOR(ViewSessionConfig vSession) throws WebAlbumsServiceException;
}
