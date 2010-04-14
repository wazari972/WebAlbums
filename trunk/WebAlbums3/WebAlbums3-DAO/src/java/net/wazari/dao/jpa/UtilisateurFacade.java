/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import net.wazari.dao.*;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevin
 */
@Stateless
public class UtilisateurFacade implements UtilisateurFacadeLocal {
    private static final Logger log = Logger.getLogger(UtilisateurFacade.class.getCanonicalName()) ;

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
        try {
            String rq = "FROM Utilisateur u WHERE u.nom = :nom";
            return (Utilisateur) em.createQuery(rq)
                    .setParameter("nom", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info("No user with name +"+name+"+");
            return null ;
        }
    }

    public Utilisateur loadUserOutside(int albumId) {
        String rq = "SELECT u FROM Utilisateur u, Album a WHERE u.ID = a.droit AND a.id = :albumId";
        return (Utilisateur) em.createQuery(rq)
                .setParameter("albumId", albumId)
                .getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Utilisateur> loadUserInside(int albumId) {
        String rq = "SELECT DISTINCT u " +
                " FROM Photo p, Utilisateur u " +
                " WHERE u.id = p.droit " +
                " AND p.album = :albumId " +
                " AND p.droit != null AND p.droit != 0";
        return  em.createQuery(rq)
                .setParameter("albumId", albumId)
                .getResultList();
    }
}
