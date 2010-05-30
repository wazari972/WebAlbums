/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import net.wazari.dao.entity.Album;

/**
 *
 * @author kevin
 */
@Stateless
public class AlbumFacade implements AlbumFacadeLocal {
    private static final Logger log = Logger.getLogger(AlbumFacade.class.getCanonicalName()) ;

    @EJB
    WebAlbumsDAOLocal webDAO;
    @PersistenceContext
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void create(Album album) {
        em.persist(album);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void edit(Album album) {
        em.merge(album);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void remove(Album album) {
        em.remove(em.merge(album));
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public List<Album> queryAlbums(ServiceSession session,
            boolean restrictAllowed,
            boolean restrictTheme, TopFirst topFirst, int topX) {
        String rq = "FROM Album a " +
                " WHERE " + (restrictAllowed ? webDAO.restrictToAlbumsAllowed(session, "a") : "1 = 1") + " " +
                " AND " + (restrictTheme ? webDAO.restrictToThemeAllowed(session, "a") : "1 = 1") + " " +
                " ORDER BY a.date DESC ";
        log.info(rq);
        Query q = em.createQuery(rq) ;
        if (topFirst == TopFirst.TOP) {
            q.setFirstResult(1);
            q.setMaxResults(topX);
        } else {
            q.setFirstResult(topX);
            q.setMaxResults(session.getAlbumSize());
        }
        return q.getResultList();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Album loadIfAllowed(ServiceSession session, int id) {
        try {
            String rq = "FROM Album a " +
                    " WHERE " + webDAO.restrictToAlbumsAllowed(session, "a") + " " +
                    " AND a.id = :id ";

            return (Album) em.createQuery(rq).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public Album loadByNameDate(String name, String date) {
        String rq = "FROM Album a " +
                " WHERE a.date = :date " +
                " AND a.nom = :nom";
        return (Album) em.createQuery(rq)
                .setParameter("date", date)
                .setParameter("nom", name)
                .getSingleResult();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Album find(Integer albumId) {
        try {
            String rq = "SELECT a FROM Album a WHERE a.id = :id";
        return (Album) em.createQuery(rq)
                .setParameter("id", albumId)
                .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }
}
