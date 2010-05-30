/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
public interface AlbumLocal {
    XmlBuilder displayAlbum(XmlBuilder output, ViewSessionAlbumDisplay vSession, XmlBuilder submit, XmlBuilder thisPage) throws WebAlbumsServiceException;

    XmlBuilder treatALBM(ViewSessionAlbum vSession) throws WebAlbumsServiceException;

    XmlBuilder treatAlbmDISPLAY(ViewSessionAlbumDisplay vSession, XmlBuilder submit) throws WebAlbumsServiceException;

    XmlBuilder treatAlbmEDIT(ViewSessionAlbumEdit vSession, XmlBuilder submit) throws WebAlbumsServiceException;

    XmlBuilder treatAlbmSUBMIT(ViewSessionAlbumSubmit vSession) throws WebAlbumsServiceException;
}
