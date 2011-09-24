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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.exchange.ServiceSession.ListOrder;
import net.wazari.dao.jpa.entity.JPATheme;
import net.wazari.dao.jpa.entity.JPATheme_;

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
    public Predicate getRestrictionToCurrentTheme(ServiceSession session, 
                                                  Path<JPATheme> albm) {
        return getRestrictionToCurrentTheme(session, albm, Restriction.THEME_ONLY) ;
    }
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Predicate getRestrictionToCurrentTheme(ServiceSession session, 
            Path<JPATheme> theme, Restriction restrict) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.conjunction() ;
        if (restrict != Restriction.NONE)
            return TRUE ;
        
        if (session.isRootSession()) {
            return TRUE ;
        } else {
            //albm.theme = session.getTheme().getId()
            return cb.equal(theme.get(JPATheme_.id),
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
