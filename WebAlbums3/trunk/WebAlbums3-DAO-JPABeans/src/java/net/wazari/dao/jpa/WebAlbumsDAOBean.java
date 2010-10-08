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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.exchange.ServiceSession.ListOrder;
import net.wazari.dao.jpa.entity.JPAAlbum;
import net.wazari.dao.jpa.entity.JPAPhoto;
import net.wazari.dao.jpa.entity.metamodel.JPAAlbum_;
import net.wazari.dao.jpa.entity.metamodel.JPAPhoto_;
import net.wazari.dao.jpa.entity.metamodel.JPATheme_;
import net.wazari.dao.jpa.entity.metamodel.JPAUtilisateur_;

/**
 *
 * @author kevin
 */
@Stateless
public class WebAlbumsDAOBean {
    private static final Logger log = LoggerFactory.getLogger(WebAlbumsDAOBean.class.getName());
    
    public static final String PERSISTENCE_UNIT_DERBY = "WebAlbums-Derby" ;
    public static final String PERSISTENCE_UNIT_MySQL = "WebAlbums-MySQL" ;
    public static final String PERSISTENCE_UNIT_MySQL_Test = "WebAlbums-MySQL-Test" ;
    public static final String PERSISTENCE_UNIT_MySQL_Test2 = "WebAlbums-MySQL-Test2" ;
    public static final String PERSISTENCE_UNIT = PERSISTENCE_UNIT_MySQL ;

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Predicate getRestrictionToAlbumsAllowed(ServiceSession session, Root<JPAAlbum> album, Restriction restrict) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.nullLiteral(Boolean.class).isNotNull() ;

        if (!(restrict == Restriction.ALLOWED_AND_THEME || restrict == Restriction.ALLOWED_ONLY)) return TRUE ;

        CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;
        Root<JPAAlbum> albm = cq.from(JPAAlbum.class);

        Predicate where = TRUE ;
        if (!session.isSessionManager()) {
            //FROM JPAAlbum a, JPAPhoto p
            Root<JPAPhoto> photo = cq.from(JPAPhoto.class);
            where = cb.and(
                        //a.id = p.album
                        cb.equal(albm.<Integer>get("id"), photo.<Integer>get("id")),
                        //and
                        cb.or(
                            cb.and(
                                cb.or(
                                    //p.droit is null
                                    cb.isNull(photo.get("droit")),
                                    //or
                                    //p.droit = 0
                                    cb.equal(photo.get("droit"), 0)
                                    ),
                                //and
                                //a.droit >= session.getUser().getId()
                                cb.greaterThanOrEqualTo(albm.get("droit").<Integer>get("id"), session.getUser().getId())
                                )
                            ),
                            //or
                            //p.droit >=  session.getUser().getId()
                            cb.greaterThanOrEqualTo(photo.<Integer>get("droit"), session.getUser().getId())
                        ) ;
        }

        cq.where(cb.and(where,
                       getRestrictionToCurrentTheme(session, albm)
                       ));
        return album.get("id").in(cq) ;
    }

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Predicate getRestrictionToPhotosAllowed(ServiceSession session, Root<JPAPhoto> photo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.nullLiteral(Boolean.class).isNotNull() ;

        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class) ;

        //FROM JPAPhoto p, JPAAlbum a
        Root<JPAAlbum> ralbm = cq.from(JPAAlbum.class);
        Root<JPAPhoto> rphoto = cq.from(JPAPhoto.class);
        //SELECT p.id
        cq.select(rphoto.<Integer>get("id")) ;
        cq.where(cb.and(
                    //a.id = p.albumString
                    cb.equal(ralbm.get("id"), rphoto.get("id")),
                    session.isSessionManager() ?
                        TRUE :
                        cb.and(
                            //a.id = p.album
                            cb.equal(ralbm.get("id"), rphoto.get("id")),
                            //and
                            cb.or(
                                cb.and(
                                    cb.or(
                                        //p.droit is null
                                        cb.isNull(rphoto.get("droit")),
                                        //or
                                        //p.droit = 0
                                        cb.equal(rphoto.get("droit"), 0)
                                        ),
                                    //and
                                    //a.droit >= session.getUser().getId()
                                    cb.greaterThanOrEqualTo(ralbm.get("droit").<Integer>get("id"), session.getUser().getId())
                                    )
                                ),
                                //or
                                //p.droit >=  session.getUser().getId()
                                cb.greaterThanOrEqualTo(rphoto.<Integer>get("droit"), session.getUser().getId())
                            ),
                            getRestrictionToCurrentTheme(session, ralbm)
                        )
                    ) ;
        return photo.<Integer>get("id").in(cq) ;
        
    }

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Predicate getRestrictionToCurrentTheme(ServiceSession session, Root<JPAAlbum> albm) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.nullLiteral(Boolean.class).isNotNull() ;

        if (session.isRootSession()) {
            return TRUE ;
        } else {
            //albm.theme = session.getTheme().getId()
            return cb.equal(albm.get("theme").get("id"),
                            session.getTheme().getId()) ;
        }

    }

    @Deprecated
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public StringBuilder processListID(ServiceSession session, StringBuilder rq, boolean restrict) {
        StringBuilder newRq = new StringBuilder(rq) ;
        if (restrict && ! session.isRootSession()) {
            newRq.append(" AND ").append(restrictToThemeAllowed(session, "a"));
        }
        return newRq;
    }

    @Deprecated
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public StringBuilder restrictToAlbumsAllowed(ServiceSession session, String album) {
        StringBuilder rq = null;
        if (session.isSessionManager()) {
            rq = new StringBuilder("SELECT a.id FROM JPAAlbum a WHERE 1 = 1 ");
        } else {
            rq = new StringBuilder(80)
                .append("SELECT a.id ")
                .append("FROM JPAAlbum a, JPAPhoto p ")
                .append("WHERE a.id = p.album AND (")
                    //albums autorisé
                .append("((p.droit = 0 OR p.droit is null) AND a.droit >= '")
                .append(session.getUser().getId())
                .append("') OR ")
                    //albums ayant au moins une photo autorisée
                .append("(p.droit >= '")
                .append(session.getUser().getId())
                .append("')")
                .append(") ");
        }
        return new StringBuilder(50).append(" ").append(album).append(".id IN (").append(processListID(session, rq, true) ).append(") ");
    }

    @Deprecated
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public String restrictToPhotosAllowed(ServiceSession session, String photo) {
        StringBuilder rq = new StringBuilder(80);
        rq.append("SELECT p.id ")
            .append(" FROM JPAPhoto p, JPAAlbum a ")
            .append(" WHERE p.album = a.id ");
        if (!session.isSessionManager()) {
            rq.append(" AND (")
                    //albums autorisé
            .append("((p.droit = 0 OR p.droit is null) AND a.droit >= '")
            .append(session.getUser().getId())
            .append("') OR ")
                    //albums ayant au moins une photo autorisée
            .append("(p.droit >= '")
            .append(session.getUser().getId())
            .append("')" )
            .append(")");
        }

        return new StringBuilder(50)
            .append(" ")
            .append(photo )
            .append(".id IN (" )
            .append(processListID(session, rq, true) )
            .append(") ").toString();
    }

    @Deprecated
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public String restrictToThemeAllowed(ServiceSession session, String album) {
        if (session.isRootSession()) {
            return " 1 = 1 ";
        } else {
            return new StringBuilder(25)
            .append(" " )
            .append(album )
            .append(".theme = '")
            .append( session.getTheme().getId())
            .append( "' ").toString();
        }

    }

    @Deprecated
    static StringBuilder getOrder(ListOrder order, String field) {
        StringBuilder rq = new StringBuilder(25)
            .append(" ORDER BY ") ;
        if (order == null) return new StringBuilder("") ;
        switch(order) {
            case ASC: return rq
            .append(field)
            .append(" ASC") ;
            case DESC: return rq
            .append(field)
            .append(" DESC") ;
            case RANDOM: return rq
            .append(" RAND()") ;
            case DEFAULT:
            default: return new StringBuilder("") ;
        }
    }

}
