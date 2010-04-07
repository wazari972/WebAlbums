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
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevin
 */
@Stateless
public class UtilisateurFacade implements UtilisateurFacadeLocal {
    @PersistenceContext
    private EntityManager em;

    public void create(Utilisateur utilisateur) {
        em.persist(utilisateur);
    }

    public void edit(Utilisateur utilisateur) {
        em.merge(utilisateur);
    }

    public void remove(Utilisateur utilisateur) {
        em.remove(em.merge(utilisateur));
    }

    public Utilisateur find(Object id) {
        return em.find(Utilisateur.class, id);
    }

    public List<Utilisateur> findAll() {
        return em.createQuery("select object(o) from Utilisateur as o").getResultList();
    }


    public Utilisateur loadByName(String name) {
        String rq = "FROM Utilisateur WHERE nom = :nom";
        return (Utilisateur) em.createQuery(rq)
                .setParameter("nom", name)
                .getSingleResult();
    }

    public Utilisateur loadUserOutside(int albumId) {
        String rq = "SELECT u FROM Utilisateur u, Album a WHERE u.ID = a.Droit AND a.ID = :albumId";
        return (Utilisateur) em.createQuery(rq)
                .setParameter("albumId", albumId)
                .getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Utilisateur> loadUserInside(int albumId) {
        String rq = "SELECT DISTINCT u " +
                " FROM Photo p, Utilisateur u " +
                " WHERE u.Id = p.Droit " +
                " AND p.Album = :albumId " +
                " AND p.Droit != null AND p.Droit != 0";
        return  em.createQuery(rq)
                .setParameter("albumId", albumId)
                .getResultList();
    }
}
