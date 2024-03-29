/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.AlbumFacadeLocal.TopFirst;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.exchange.ServiceSession.ListOrder;
import net.wazari.dao.jpa.entity.JPAAlbum;
import net.wazari.dao.jpa.entity.JPAAlbum_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public class AlbumFacade implements AlbumFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(AlbumFacade.class.getCanonicalName()) ;

    @EJB
    WebAlbumsDAOBean webDAO;

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void create(Album album) {
        em.persist(album);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void edit(Album album) {
        em.merge(album);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void remove(Album album) {
        em.remove(album);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public SubsetOf<Album> queryAlbums(ServiceSession session,
            AlbumFacadeLocal.Restriction restrict, AlbumFacadeLocal.TopFirst topFirst, Bornes bornes) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;

        Root<JPAAlbum> albm = cq.from(JPAAlbum.class);
        cq.where(webDAO.getRestrictionToCurrentTheme(session, 
                albm.get(JPAAlbum_.theme), restrict)) ;
        cq.orderBy(cb.desc(albm.get(JPAAlbum_.date))) ;
        TypedQuery<JPAAlbum> q = em.createQuery(cq);

        int size = q.getResultList().size() ;
        if (topFirst == TopFirst.TOP) {
            q.setFirstResult(0);
            q.setMaxResults(bornes.getNbElement());
        } else if (topFirst == TopFirst.FIRST) {
            q.setFirstResult(bornes.getFirstElement());
            q.setMaxResults(session.getPhotoAlbumSize());
        }
        q.setHint("org.hibernate.cacheable", true) ;
        q.setHint("org.hibernate.readOnly", true) ;

        List<JPAAlbum> lstAlbums = q.getResultList() ;
        
        lstAlbums = webDAO.filterAlbumsAllowed(lstAlbums, session) ;
        
        return (SubsetOf) new SubsetOf<>(bornes, lstAlbums, (long) size);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public SubsetOf<Album> queryRandomFromYear(ServiceSession session,
        Restriction restrict, Bornes bornes, String date) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        
        CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;
        //FROM album
        Root<JPAAlbum> albm = cq.from(JPAAlbum.class);
        //where restrict to theme and albums
        cq.where( cb.and(
                webDAO.getRestrictionToCurrentTheme(session, 
                        albm.get(JPAAlbum_.theme), restrict),
                cb.like(albm.get(JPAAlbum_.date), date+"%")
                )) ;
        
        webDAO.setOrder(cq, cb, ListOrder.RANDOM, null) ;
        
        Query q = em.createQuery(cq)
               .setFirstResult(0)
               .setMaxResults(bornes.getNbElement());
        q.setHint("org.hibernate.cacheable", true)
         .setHint("org.hibernate.readOnly", true) ;
        Long size = (long) bornes.getNbElement() ;

        List<JPAAlbum> lstAlbums = q.getResultList() ;
        
        lstAlbums = webDAO.filterAlbumsAllowed(lstAlbums, session) ;
        
        return (SubsetOf) new SubsetOf<>(bornes,lstAlbums, size);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Album loadFirstAlbum(ServiceSession session,
            Restriction restrict) {
        return loadFirstLastAlbum(session, restrict, ListOrder.ASC) ;
    }
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Album loadLastAlbum(ServiceSession session,
            Restriction restrict) {
        return loadFirstLastAlbum(session, restrict, ListOrder.DESC) ;
    }

    private Album loadFirstLastAlbum(ServiceSession session,
            Restriction restrict, ListOrder order) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;
            Root<JPAAlbum> albm = cq.from(JPAAlbum.class);
            cq.where(cb.and(
                    webDAO.getRestrictionToCurrentTheme(session, 
                    albm.get(JPAAlbum_.theme), restrict))) ;
            webDAO.setOrder(cq, cb, order, albm.get(JPAAlbum_.date)) ;

            Query q = em.createQuery(cq)
                   .setFirstResult(0)
                   .setMaxResults(1)
                   .setHint("org.hibernate.cacheable", true)
                   .setHint("org.hibernate.readOnly", true) ;

            return webDAO.filter((JPAAlbum) q.getSingleResult(), session);
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Album loadIfAllowed(ServiceSession session, int id) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;
            Root<JPAAlbum> albm = cq.from(JPAAlbum.class);
            cq.where(cb.equal(albm.get(JPAAlbum_.id), id)) ;
            JPAAlbum a = (JPAAlbum) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", false)
                    .getSingleResult();
            return webDAO.filter(a, session);
        } catch (NoResultException e) {
            return null ;
        }
    }
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Album find(Integer id) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;
            Root<JPAAlbum> a = cq.from(JPAAlbum.class);
            cq.where(cb.equal(a.get(JPAAlbum_.id), id)) ;
            return (JPAAlbum) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Album loadByNameDate(String name, String date) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;
            Root<JPAAlbum> albm = cq.from(JPAAlbum.class);
            cq.where(cb.and(cb.equal(albm.get(JPAAlbum_.date), date),
                    cb.equal(albm.get(JPAAlbum_.nom), name)));
            return (JPAAlbum) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public Album newAlbum() {
        return new JPAAlbum();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public List<Album> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;
        cq.from(JPAAlbum.class) ;
        return (List) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public List<Album> loadTimesAgoAlbums(ServiceSession session, 
                                          Integer year, Integer month, Integer day,
                                          Restriction restrict) {
        String dateTempl = (year != null ? String.format("%04d", year) : "%%%%") + "-" +
                           (month != null ? String.format("%02d", month) : "%%") + "-" +
                           (day != null ? String.format("%02d", day) : "%%") ;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;
        Root<JPAAlbum> albm = cq.from(JPAAlbum.class) ;
        cq.where(cb.and(
                    cb.like(albm.get(JPAAlbum_.date), dateTempl),
                    webDAO.getRestrictionToCurrentTheme(session, 
                    albm.get(JPAAlbum_.theme), restrict))) ;
        cq.orderBy(cb.desc(albm.get(JPAAlbum_.date))) ;
        return (List) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }
}
