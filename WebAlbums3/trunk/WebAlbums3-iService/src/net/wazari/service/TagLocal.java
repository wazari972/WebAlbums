/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.common.util.XmlBuilder;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE, UserLocal.MANAGER_ROLE})
public interface TagLocal {
    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatPhotoSUBMIT(ViewSessionPhotoSubmit vSession, Boolean correct) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatTagDISPLAY(ViewSessionTag vSession, XmlBuilder submit) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatTagEDIT(ViewSessionTag vSession, XmlBuilder submit) throws WebAlbumsServiceException ;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatTagPersonsPlaces(ViewSessionTag vSession);

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatTagCloud(ViewSessionTag vSession);

}
