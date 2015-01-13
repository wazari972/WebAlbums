/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.dao.exchange.ServiceSession;

/**
 *
 * @author kevin
 */
@Local
public interface AlbumFacadeLocal {

    public enum TopFirst {
        TOP, FIRST, ALL
    }

    public enum Restriction {
        THEME_ONLY, NONE
    }
    
    List<Album> findAll();

    void create(Album album);

    void edit(Album album);

    void remove(Album album);

    Album find(Integer albumId);

    Album newAlbum();
    
    SubsetOf<Album> queryAlbums(ServiceSession session,
            Restriction restrict, TopFirst topFirst, Bornes bornes) ;

    SubsetOf<Album> queryRandomFromYear(ServiceSession session,
            Restriction restrict, Bornes bornes, String date);

    Album loadFirstAlbum(ServiceSession session, Restriction restrict) ;
    Album loadLastAlbum(ServiceSession session, Restriction restrict) ;
    
    Album loadIfAllowed(ServiceSession session, int id) ;

    Album loadByNameDate(String name, String date) ;
    
    List<Album> loadTimesAgoAlbums(ServiceSession session, Integer year, 
            Integer month, Integer day, Restriction restrict) ;
}
