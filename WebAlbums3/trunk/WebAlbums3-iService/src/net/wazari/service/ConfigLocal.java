/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.common.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.MANAGER_ROLE})
public interface ConfigLocal {
    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatDELTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatIMPORT(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatMODGEO(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatMODTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatMODVIS(ViewSessionConfig vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatNEWTAG(ViewSessionConfig vSession) throws WebAlbumsServiceException;

}
