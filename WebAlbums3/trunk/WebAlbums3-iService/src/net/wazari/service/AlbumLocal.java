/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.common.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE, UserLocal.MANAGER_ROLE})
public interface AlbumLocal {
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayAlbum(XmlBuilder output, ViewSessionAlbumDisplay vSession, XmlBuilder submit, XmlBuilder thisPage) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatAlbmDISPLAY(ViewSessionAlbumDisplay vSession, XmlBuilder submit) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatAlbmEDIT(ViewSessionAlbumEdit vSession, XmlBuilder submit) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlBuilder treatAlbmSUBMIT(ViewSessionAlbumSubmit vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatTOP(ViewSessionAlbum vSession);

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder treatYEARS(ViewSessionAlbum vSession);
}
