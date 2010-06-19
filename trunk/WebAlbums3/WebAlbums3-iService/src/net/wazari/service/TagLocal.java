/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.common.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
@RolesAllowed({UserLocal.VIEWER_ROLE})
public interface TagLocal {

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatTAGS(ViewSessionTag vSession) throws WebAlbumsServiceException;

}
