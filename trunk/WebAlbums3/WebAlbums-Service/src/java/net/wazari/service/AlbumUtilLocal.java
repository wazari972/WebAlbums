/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.dao.entity.Album;
import net.wazari.service.exception.WebAlbumsServiceException;

/**
 *
 * @author kevin
 */
@Local
public interface AlbumUtilLocal {

    void setDateStr(Album a, String date) throws WebAlbumsServiceException;

    void setNom(Album a, String nom) throws WebAlbumsServiceException;

    void updateDroit(Album a, Integer droit) throws WebAlbumsServiceException;

    void setTagsToPhoto(Album enrAlbum, Integer[] tags, Boolean force) throws WebAlbumsServiceException;

}
