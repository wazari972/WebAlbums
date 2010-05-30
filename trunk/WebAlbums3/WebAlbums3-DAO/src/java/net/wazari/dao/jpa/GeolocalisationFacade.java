/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import net.wazari.dao.*;
import javax.annotation.security.RolesAllowed;
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

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void create(Geolocalisation geolocalisation) {
        em.persist(geolocalisation);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void edit(Geolocalisation geolocalisation) {
        em.merge(geolocalisation);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void remove(Geolocalisation geolocalisation) {
        em.remove(em.merge(geolocalisation));
    }
}
