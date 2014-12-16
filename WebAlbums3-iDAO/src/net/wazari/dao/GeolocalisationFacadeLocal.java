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
public interface GeolocalisationFacadeLocal {
    Geolocalisation newGeolocalisation() ;
    
    void create(Geolocalisation geolocalisation);

    void edit(Geolocalisation geolocalisation);

    void remove(Geolocalisation geolocalisation);
}
