/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.common.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE})
public interface ImageLocal {

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatIMG(ViewSessionImages vSession) throws WebAlbumsServiceException;

}