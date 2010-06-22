/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import net.wazari.dao.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Geolocalisation;
import net.wazari.dao.jpa.entity.JPAGeolocalisation;

/**
 *
 * @author kevin
 */
@Stateless
public class GeolocalisationFacade implements GeolocalisationFacadeLocal {
    @PersistenceContext
    private EntityManager em;

    @Override
    public void create(Geolocalisation geolocalisation) {
        em.persist(geolocalisation);
    }

    @Override
    public void edit(Geolocalisation geolocalisation) {
        em.merge(geolocalisation);
    }

    @Override
    public void remove(Geolocalisation geolocalisation) {
        em.remove(em.merge(geolocalisation));
    }

    @Override
    public Geolocalisation newGeolocalisation() {
        return new JPAGeolocalisation();
    }
}
