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
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumAgo;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSelect;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSimple;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumYear;
import net.wazari.service.exchange.xml.album.*;
import net.wazari.service.exchange.xml.common.XmlFrom;

/**
 *
 * @author kevin
 */
@Local
public interface AlbumLocal {
    XmlAlbumList displayAlbum(ViewSessionAlbumDisplay vSession, XmlAlbumSubmit submit, XmlFrom fromPage) throws WebAlbumsServiceException;

    XmlAlbumDisplay treatAlbmDISPLAY(ViewSessionAlbumDisplay vSession, XmlAlbumSubmit submit) throws WebAlbumsServiceException;

    XmlAlbum treatAlbmEDIT(ViewSessionAlbumEdit vSession) throws WebAlbumsServiceException;

    XmlAlbumSubmit treatAlbmSUBMIT(ViewSessionAlbumSubmit vSession) throws WebAlbumsServiceException;

    XmlAlbumTop treatTOP(ViewSession vSession) throws WebAlbumsServiceException;

    XmlAlbumYears treatYEARS(ViewSessionAlbumYear vSession) throws WebAlbumsServiceException;

    XmlAlbumSelect treatSELECT(ViewSessionAlbumSelect vSession) throws WebAlbumsServiceException;
    
    XmlAlbumGraph treatGRAPH(ViewSessionAlbumSelect vSession) throws WebAlbumsServiceException;
    
    XmlAlbumAgo treatAGO(ViewSessionAlbumAgo vSession) throws WebAlbumsServiceException;
    
    XmlAlbumAbout treatABOUT(ViewSessionAlbumSimple vSession) throws WebAlbumsServiceException ;

    XmlAlbumGpx treatGPX(ViewSession vSession) throws WebAlbumsServiceException;
}
