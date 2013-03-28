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
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumAgo;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.service.exchange.xml.album.*;
import net.wazari.service.exchange.xml.common.XmlFrom;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE, UserLocal.MANAGER_ROLE})
public interface AlbumLocal {
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlAlbumList displayAlbum(ViewSessionAlbumDisplay vSession, XmlAlbumSubmit submit, XmlFrom fromPage) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlAlbumDisplay treatAlbmDISPLAY(ViewSessionAlbumDisplay vSession, XmlAlbumSubmit submit) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlAlbum treatAlbmEDIT(ViewSessionAlbumEdit vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    XmlAlbumSubmit treatAlbmSUBMIT(ViewSessionAlbumSubmit vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlAlbumTop treatTOP(ViewSessionAlbum vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlAlbumYears treatYEARS(ViewSessionAlbum vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlAlbumSelect treatSELECT(ViewSessionAlbum vSession) throws WebAlbumsServiceException;
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlAlbumGraph treatGRAPH(ViewSessionAlbum vSession) throws WebAlbumsServiceException;
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlAlbumAgo treatAGO(ViewSessionAlbumAgo vSession) throws WebAlbumsServiceException;
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlAlbumAbout treatABOUT(ViewSessionAlbum vSession) throws WebAlbumsServiceException ;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlAlbumGpx treatGPX(ViewSessionAlbum vSession) throws WebAlbumsServiceException;
}
