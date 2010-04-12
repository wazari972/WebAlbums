/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Album;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.util.XmlBuilder;

/**
 *
 * @author kevin
 */
@Local
public interface AlbumLocal {

    XmlBuilder displayAlbum(List<Album> query, XmlBuilder output, ViewSessionAlbum vSession, XmlBuilder submit, XmlBuilder thisPage) throws WebAlbumsServiceException;

    XmlBuilder treatALBM(ViewSessionAlbum vSession) throws WebAlbumsServiceException;

    XmlBuilder treatAlbmDISPLAY(ViewSessionAlbum vSession, XmlBuilder submit) throws WebAlbumsServiceException;

    XmlBuilder treatAlbmEDIT(ViewSessionAlbum vSession, XmlBuilder submit) throws WebAlbumsServiceException;

    XmlBuilder treatAlbmSUBMIT(ViewSessionAlbum vSession) throws WebAlbumsServiceException;

}
