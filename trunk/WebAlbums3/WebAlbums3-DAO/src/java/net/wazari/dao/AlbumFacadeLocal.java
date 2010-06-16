/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import javax.annotation.security.RolesAllowed;
import net.wazari.dao.exchange.ServiceSession;
import javax.ejb.Local;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;

/**
 *
 * @author kevin
 */
@Local
public interface AlbumFacadeLocal {
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void create(Album album);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void edit(Album album);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void remove(Album album);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Album find(Integer albumId);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Album newAlbum();
    
    enum TopFirst {
        TOP, FIRST
    }

    enum Restriction {
        ALLOWED_ONLY, THEME_ONLY, ALLOWED_AND_THEME
    }
    
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    SubsetOf<Album> queryAlbums(ServiceSession session,
            Restriction restrict, TopFirst topFirst, Bornes bornes) ;

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Album loadIfAllowed(ServiceSession session, int id) ;

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Album loadByNameDate(String name, String date) ;
}
