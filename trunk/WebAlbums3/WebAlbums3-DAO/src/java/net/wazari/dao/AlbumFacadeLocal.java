/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import net.wazari.dao.exchange.ServiceSession;
import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Album;

/**
 *
 * @author kevin
 */
@Local
public interface AlbumFacadeLocal {

    void create(Album album);

    void edit(Album album);

    void remove(Album album);

    Album find(Integer albumId);
    
    enum TopFirst {
        TOP, FIRST
    }

    List<Album> queryAlbums(ServiceSession session,
            boolean restrictAllowed,
            boolean restrictTheme, TopFirst topFirst, int topX) ;

    Album loadIfAllowed(ServiceSession session, int id) ;

    Album loadByNameDate(String name, String date) ;
}
