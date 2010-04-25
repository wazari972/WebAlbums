/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
public interface AlbumLocal {
    XmlBuilder displayAlbum(XmlBuilder output, ViewSessionAlbum vSession, XmlBuilder submit, XmlBuilder thisPage) throws WebAlbumsServiceException;

    XmlBuilder treatALBM(ViewSessionAlbum vSession) throws WebAlbumsServiceException;

    XmlBuilder treatAlbmDISPLAY(ViewSessionAlbum vSession, XmlBuilder submit) throws WebAlbumsServiceException;

    XmlBuilder treatAlbmEDIT(ViewSessionAlbum vSession, XmlBuilder submit) throws WebAlbumsServiceException;

    XmlBuilder treatAlbmSUBMIT(ViewSessionAlbum vSession) throws WebAlbumsServiceException;

}
