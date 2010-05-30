/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import net.wazari.dao.*;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
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
    @EJB AlbumFacadeLocal albumDAO ;
    @PersistenceContext
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void create(Utilisateur utilisateur) {
        em.persist(utilisateur);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void edit(Utilisateur utilisateur) {
        em.merge(utilisateur);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void remove(Utilisateur utilisateur) {
        em.remove(em.merge(utilisateur));
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
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

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public Utilisateur loadUserOutside(int albumId) {
        String rq = "SELECT u FROM Utilisateur u, Album a WHERE u.id = a.droit AND a.id = :albumId";
        return (Utilisateur) em.createQuery(rq)
                .setParameter("albumId", albumId)
                .getSingleResult();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public List<Utilisateur> loadUserInside(int albumId) {
        String rq = "SELECT DISTINCT u " +
                " FROM Photo p, Utilisateur u " +
                " WHERE u.id = p.droit " +
                " AND p.album.id = :albumId " +
                " AND p.droit != null AND p.droit != 0";
        return  em.createQuery(rq)
                .setParameter("albumId", albumId)
                .getResultList();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Utilisateur find(Integer droit) {
        try {
            String rq = "FROM Utilisateur u WHERE u.id = :id";
            return (Utilisateur) em.createQuery(rq)
                    .setParameter("id", droit)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        String rq = "FROM Utilisateur u";
        return em.createQuery(rq).getResultList() ;
    }
}
