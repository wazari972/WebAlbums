/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.List;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.logging.Logger;
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

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
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
            q.setFirstResult(0);
            q.setMaxResults(bornes.getNbElement());
        } else {
            q.setFirstResult(bornes.getFirstElement());
            q.setMaxResults(session.getAlbumSize());
        }
        q.setHint("org.hibernate.cacheable", true) ;
        q.setHint("org.hibernate.readOnly", true) ;
        Query qSize = em.createQuery("SELECT count(*) "+rq) ;
        Long size = (Long) qSize.getSingleResult() ;

        return new SubsetOf<Album>(bornes, q.getResultList(), size);
    }

    @Override
    public SubsetOf<Album> queryRandomFromYear(ServiceSession session,
            Restriction restrict, Bornes bornes, String date) {
        String rq = "FROM "+JPAAlbum.class.getName()+" a " +
                " WHERE " + (restrict == Restriction.ALLOWED_AND_THEME || restrict == Restriction.ALLOWED_ONLY ? webDAO.restrictToAlbumsAllowed(session, "a") : "1 = 1") + " " +
                " AND " + (restrict == Restriction.ALLOWED_AND_THEME || restrict == Restriction.THEME_ONLY ? webDAO.restrictToThemeAllowed(session, "a") : "1 = 1") + " " +
                " AND a.date LIKE :date "+
                " ORDER BY RAND() ";
        Query q = em.createQuery(rq)
               .setParameter("date", date+"%")
               .setFirstResult(0)
               .setMaxResults(bornes.getNbElement());
        q.setHint("org.hibernate.cacheable", true)
         .setHint("org.hibernate.readOnly", true) ;
        Long size = (long) bornes.getNbElement() ;

        return new SubsetOf<Album>(bornes, q.getResultList(), size);
    }

    @Override
    public Album loadFirstAlbum(ServiceSession session,
            Restriction restrict) {
        return loadFirstLastAlbum(session, restrict, ORDER.ASC) ;
    }
    
    @Override
    public Album loadLastAlbum(ServiceSession session,
            Restriction restrict) {
        return loadFirstLastAlbum(session, restrict, ORDER.DESC) ;
    }

    private enum ORDER {ASC, DESC}
    private Album loadFirstLastAlbum(ServiceSession session,
            Restriction restrict, ORDER order) {
        try {
            String rq = "FROM "+JPAAlbum.class.getName()+" a " +
                    " WHERE " + (restrict == Restriction.ALLOWED_AND_THEME || restrict == Restriction.ALLOWED_ONLY ? webDAO.restrictToAlbumsAllowed(session, "a") : "1 = 1") + " " +
                    " AND " + (restrict == Restriction.ALLOWED_AND_THEME || restrict == Restriction.THEME_ONLY ? webDAO.restrictToThemeAllowed(session, "a") : "1 = 1") + " " +
                    " ORDER BY a.date "+order;
            Query q = em.createQuery(rq)
                   .setFirstResult(0)
                   .setMaxResults(1)
                   .setHint("org.hibernate.cacheable", true)
                   .setHint("org.hibernate.readOnly", true) ;

            return (Album) q.getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public Album loadIfAllowed(ServiceSession session, int id) {
        try {
            String rq = "FROM JPAAlbum a " +
                    " WHERE " + webDAO.restrictToAlbumsAllowed(session, "a") + " " +
                    " AND a.id = :id ";

            return (JPAAlbum) em.createQuery(rq).setParameter("id", id)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", false)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public Album loadByNameDate(String name, String date) {
        try {
            String rq = "FROM JPAAlbum a " +
                    " WHERE a.date = :date " +
                    " AND a.nom = :nom";
            return (JPAAlbum) em.createQuery(rq)
                    .setParameter("date", date)
                    .setParameter("nom", name)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public Album find(Integer albumId) {
        try {
            String rq = "SELECT a FROM JPAAlbum a WHERE a.id = :id";
        return (JPAAlbum) em.createQuery(rq)
                .setParameter("id", albumId)
                .setHint("org.hibernate.cacheable", true)
                .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public Album newAlbum() {
        return new JPAAlbum();
    }

    @Override
    public List<Album> findAll() {
        String rq = "SELECT o FROM JPAAlbum o";
        return (List<Album>) em.createQuery(rq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();

    }
}
