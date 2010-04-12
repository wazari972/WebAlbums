/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import net.wazari.dao.*;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Geolocalisation;

/**
 *
 * @author kevin
 */
@Stateless
public class GeolocalisationFacade implements GeolocalisationFacadeLocal {
    @PersistenceContext
    private EntityManager em;

    public void create(Geolocalisation geolocalisation) {
        em.persist(geolocalisation);
    }

    public void edit(Geolocalisation geolocalisation) {
        em.merge(geolocalisation);
    }

    public void remove(Geolocalisation geolocalisation) {
        em.remove(em.merge(geolocalisation));
    }

    public Geolocalisation find(Object id) {
        return em.find(Geolocalisation.class, id);
    }

    public List<Geolocalisation> findAll() {
        return em.createQuery("select object(o) from Geolocalisation as o")
                .getResultList();
    }
}
