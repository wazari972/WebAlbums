/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.security.RolesAllowed;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.exchange.ServiceSession.ListOrder;
import net.wazari.dao.jpa.entity.JPAAlbum;
import net.wazari.dao.jpa.entity.JPAPhoto;
import net.wazari.dao.jpa.entity.JPAAlbum_;
import net.wazari.dao.jpa.entity.JPACarnet;
import net.wazari.dao.jpa.entity.JPACarnet_;
import net.wazari.dao.jpa.entity.JPAPhoto_;
import net.wazari.dao.jpa.entity.JPATheme_;
import net.wazari.dao.jpa.entity.JPAUtilisateur_;

/**
 *
 * @author kevin
 */
@Stateless
public class WebAlbumsDAOBean {
    private static final Logger log = LoggerFactory.getLogger(WebAlbumsDAOBean.class.getName());
    
    public static final String PERSISTENCE_UNIT_Derby = "WebAlbums-Derby" ;

    public static final String PERSISTENCE_UNIT_MySQL_Prod = "WebAlbums-MySQL" ;
    public static final String PERSISTENCE_UNIT_MySQL_Simu = "WebAlbums-MySQL-Test" ;
    public static final String PERSISTENCE_UNIT_MySQL_Test = "WebAlbums-MySQL-Test2" ;
    public static final String PERSISTENCE_UNIT_MySQL_StandAlone = "WebAlbums-MySQL-StandAlone" ;

    public static final String PERSISTENCE_UNIT_Prod = PERSISTENCE_UNIT_MySQL_Prod ;
    public static final String PERSISTENCE_UNIT = PERSISTENCE_UNIT_MySQL_Prod ;

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Predicate getRestrictionToCarnetsAllowed(ServiceSession session,
            Path<JPACarnet> carnet, Subquery<JPACarnet> sq, Restriction restrict) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.conjunction() ;
        Root<JPACarnet> c = sq.from(JPACarnet.class);
        Predicate where = TRUE ;
        
        if (!session.isSessionManager()) {
            where = cb.greaterThanOrEqualTo(carnet.get(JPACarnet_.droit).get(JPAUtilisateur_.id), session.getUser().getId()) ;
        }
        sq.where(where);
        return carnet.in(sq.select(c)) ;
    }
    
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Predicate getRestrictionToAlbumsAllowed(ServiceSession session,
            Path<JPAAlbum> album, Subquery<JPAAlbum> sq, Restriction restrict) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.conjunction() ;

        if (!(restrict == Restriction.ALLOWED_AND_THEME || restrict == Restriction.ALLOWED_ONLY)) return TRUE ;

        Root<JPAAlbum> albm = sq.from(JPAAlbum.class);
        Predicate where = TRUE ;
        if (!session.isSessionManager()) {
            //FROM JPAAlbum a, JPAPhoto p
            ListJoin<JPAAlbum, JPAPhoto> photo = albm.join(JPAAlbum_.jPAPhotoList) ;
            where = cb.and(
                        cb.or(
                            cb.and(
                                cb.or(
                                    //p.droit is null
                                    cb.isNull(photo.get(JPAPhoto_.droit)),
                                    //or
                                    //p.droit = 0
                                    cb.equal(photo.get(JPAPhoto_.droit), 0)
                                ),
                                //and
                                //a.droit >= session.getUser().getId()
                                cb.greaterThanOrEqualTo(albm.get(JPAAlbum_.droit).get(JPAUtilisateur_.id), 
                                                        session.getUser().getId())
                            )
                        ),
                        //and
                        //p.droit >=  session.getUser().getId()
                        cb.greaterThanOrEqualTo(photo.get(JPAPhoto_.droit), session.getUser().getId())
                        ) ;
        }
        if (restrict == Restriction.ALLOWED_AND_THEME)
            where = cb.and(where, getRestrictionToCurrentTheme(session, album, restrict)) ;
        sq.where(where);
        return album.in(sq.select(albm)) ;
    }

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Predicate getRestrictionToPhotosAllowed(ServiceSession session,
            Path<JPAPhoto> photo, Subquery<JPAPhoto> sq) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.conjunction() ;

        //FROM JPAPhoto p, JPAAlbum a
        Root<JPAPhoto> p = sq.from(JPAPhoto.class);
        Join<JPAPhoto, JPAAlbum> a = p.join(JPAPhoto_.album) ;
        //SELECT p.id
        Predicate where = TRUE ;
        if (!session.isSessionManager()) {
            where = 
                cb.or(
                    cb.and(
                        cb.or(
                            //p.droit is null
                            cb.isNull(p.get(JPAPhoto_.droit)),
                            //or
                            //p.droit = 0
                            cb.equal(p.get(JPAPhoto_.droit), 0)
                        ),
                        //and
                        //a.droit >= session.getUser().getId()
                        cb.greaterThanOrEqualTo(a.get(JPAAlbum_.droit).get(JPAUtilisateur_.id), session.getUser().getId())
                    ),
                    //or
                    //p.droit >=  session.getUser().getId()
                    cb.greaterThanOrEqualTo(p.get(JPAPhoto_.droit), session.getUser().getId())     
            ) ;
        }
        sq.where(where);
        return photo.in(sq.select(p)) ;
    }

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Predicate getRestrictionToCurrentTheme(ServiceSession session, 
            Path<JPAAlbum> albm, Restriction restrict) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.conjunction() ;
        if (!(restrict == Restriction.ALLOWED_AND_THEME
           || restrict == Restriction.THEME_ONLY))
            return TRUE ;
        
        if (session.isRootSession()) {
            return TRUE ;
        } else {
            //albm.theme = session.getTheme().getId()
            return cb.equal(albm.get(JPAAlbum_.theme).get(JPATheme_.id),
                            session.getTheme().getId()) ;
        }

    }

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public void setOrder(CriteriaQuery<?> cq, CriteriaBuilder cb,
            ListOrder order, Expression<?> field) {
        if (order == null) return ;

        Order orderBy = null ;
        switch(order) {
            case ASC: orderBy = cb.asc(field) ; break ;
            case DESC: orderBy = cb.desc(field) ; break ;
            case RANDOM: orderBy = cb.asc(cb.function("RAND", Float.class)) ; break ;
            case DEFAULT:
            default: return ;
        }
        cq.orderBy(orderBy) ;
    }
}
