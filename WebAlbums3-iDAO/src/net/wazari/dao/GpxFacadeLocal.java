/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao;

import javax.annotation.security.RolesAllowed;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Gpx;

/**
 *
 * @author kevin
 */
public interface GpxFacadeLocal {
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Gpx find(Integer gpxId);
    
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void create(Gpx gpx);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void edit(Gpx gpx);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Gpx newGpx(Album enrAlbum);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void remove(Gpx gpx);
    
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Gpx loadByPath(String path);
}
