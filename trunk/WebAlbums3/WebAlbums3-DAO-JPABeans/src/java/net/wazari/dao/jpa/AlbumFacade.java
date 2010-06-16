/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.dao.jpa.entity.JPAAlbum;

/**
 *
 * @author kevin
 */
@Stateless
public class AlbumFacade implements AlbumFacadeLocal {
    private static final Logger log = Logger.getLogger(AlbumFacade.class.getCanonicalName()) ;

    @EJB
    WebAlbumsDAOBean webDAO;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void create(Album album) {
        em.persist(album);
    }

    @Override
    public void edit(Album album) {
        em.merge(album);
    }

    @Override
    public void remove(Album album) {
        em.remove(em.merge(album));
    }

    @Override
    public SubsetOf<Album> queryAlbums(ServiceSession session,
            Restriction restrict, TopFirst topFirst, Bornes bornes) {
        String rq = "FROM "+JPAAlbum.class.getName()+" a " +
                " WHERE " + (restrict == Restriction.ALLOWED_AND_THEME || restrict == Restriction.ALLOWED_ONLY ? webDAO.restrictToAlbumsAllowed(session, "a") : "1 = 1") + " " +
                " AND " + (restrict == Restriction.ALLOWED_AND_THEME || restrict == Restriction.THEME_ONLY ? webDAO.restrictToThemeAllowed(session, "a") : "1 = 1") + " " +
                " ORDER BY a.date DESC ";
        Query q = em.createQuery(rq) ;
        if (topFirst == TopFirst.TOP) {
            q.setFirstResult(1);
            q.setMaxResults(bornes.getNbElement());
        } else {
            q.setFirstResult(bornes.getFirstElement());
            q.setMaxResults(session.getAlbumSize());
        }

        Query qSize = em.createQuery("SELECT count(*) "+rq) ;
        Long size = (Long) qSize.getSingleResult() ;

        return new SubsetOf<Album>(bornes, q.getResultList(), size);
    }

    @Override
    public Album loadIfAllowed(ServiceSession session, int id) {
        try {
            String rq = "FROM JPAAlbum a " +
                    " WHERE " + webDAO.restrictToAlbumsAllowed(session, "a") + " " +
                    " AND a.id = :id ";

            return (JPAAlbum) em.createQuery(rq).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public Album loadByNameDate(String name, String date) {
        String rq = "FROM JPAAlbum a " +
                " WHERE a.date = :date " +
                " AND a.nom = :nom";
        return (JPAAlbum) em.createQuery(rq)
                .setParameter("date", date)
                .setParameter("nom", name)
                .getSingleResult();
    }

    @Override
    public Album find(Integer albumId) {
        try {
            String rq = "SELECT a FROM JPAAlbum a WHERE a.id = :id";
        return (JPAAlbum) em.createQuery(rq)
                .setParameter("id", albumId)
                .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public Album newAlbum() {
        return new JPAAlbum();
    }
}
