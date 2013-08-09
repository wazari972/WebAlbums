/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetDisplay;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetEdit;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetSubmit;
import net.wazari.service.exchange.xml.carnet.XmlCarnetEdit;
import net.wazari.service.exchange.xml.carnet.XmlCarnetSubmit;
import net.wazari.service.exchange.xml.carnet.XmlCarnetsDisplay;
import net.wazari.service.exchange.xml.carnet.XmlCarnetsTop;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE, UserLocal.MANAGER_ROLE})
public interface CarnetLocal {
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlCarnetsDisplay treatDISPLAY(ViewSessionCarnetDisplay vSession, XmlCarnetSubmit submit) throws WebAlbumsServiceException;
    
    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlCarnetEdit treatEDIT(ViewSessionCarnetEdit vSession, XmlCarnetSubmit submit) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlCarnetSubmit treatSUBMIT(ViewSessionCarnetSubmit vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlCarnetsTop treatTOP(ViewSession vSession);
}
