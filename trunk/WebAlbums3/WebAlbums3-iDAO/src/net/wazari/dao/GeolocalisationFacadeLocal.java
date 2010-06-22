/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Geolocalisation;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.ADMIN_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface GeolocalisationFacadeLocal {

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Geolocalisation newGeolocalisation() ;
    
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void create(Geolocalisation geolocalisation);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void edit(Geolocalisation geolocalisation);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void remove(Geolocalisation geolocalisation);
}
