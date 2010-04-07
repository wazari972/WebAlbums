/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Geolocalisation;

/**
 *
 * @author kevin
 */
@Local
public interface GeolocalisationFacadeLocal {

    void create(Geolocalisation geolocalisation);

    void edit(Geolocalisation geolocalisation);

    void remove(Geolocalisation geolocalisation);

    Geolocalisation find(Object id);

    List<Geolocalisation> findAll();
}
