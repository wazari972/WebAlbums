/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.ArrayList;
import net.wazari.dao.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.jpa.entity.JPATheme;
import net.wazari.dao.jpa.entity.JPATheme_;

/**
 *
 * @author kevin
 */
@Stateless
public class ThemeFacade implements ThemeFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(ThemeFacade.class.getName());
     
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    public void remove(Theme theme, boolean protect) {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT != WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod) {
            throw new IllegalStateException("cannot remove a theme while ProtectedDB is enabled") ;
        }
        em.remove(em.merge(theme));
    }

    @Override
    public List<Theme> findAll() {
        try {
            //SELECT object(o) FROM JPATheme AS o
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPATheme> cq = cb.createQuery(JPATheme.class);
            Root<JPATheme> th = cq.from(JPATheme.class);
            return (List) em.createQuery(cq.select(th))
                    .getResultList();
        } catch (javax.persistence.PersistenceException e) {
            log.warn("Database query failed ...");
            return new ArrayList<Theme>() ;
        }
    }

    @Override
    public JPATheme loadByName(String themeName) {
        try {
            //FROM JPATheme t WHERE t.nom = :nom
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPATheme> cq = cb.createQuery(JPATheme.class);
            Root<JPATheme> from = cq.from(JPATheme.class);
            CriteriaQuery<JPATheme> select = cq.select(from);
            cq.where(cb.equal(from.get(JPATheme_.nom), themeName)) ;
            TypedQuery<JPATheme> tq = em.createQuery(select);
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public JPATheme find(Integer id) {
        try {
            //FROM JPATheme t WHERE t.id = :id
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPATheme> cq = cb.createQuery(JPATheme.class);
            Root<JPATheme> from = cq.from(JPATheme.class);
            CriteriaQuery<JPATheme> select = cq.select(from);
            cq.where(cb.equal(from.get(JPATheme_.id), id)) ;
            TypedQuery<JPATheme> tq = em.createQuery(select);
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public Theme newTheme(int id, String name) {
        Theme enrTheme = new JPATheme(id, name) ;
        
        return em.merge(enrTheme) ;
    }

    @Override
    public Theme newTheme(String name) {
        Theme enrTheme = new JPATheme() ;
        enrTheme.setNom(name) ;
        
        return em.merge(enrTheme) ;
    }

    @Override
    public void setPicture(Theme enrTheme, Integer pict) {
        if (enrTheme != null) {
            enrTheme.setPicture(pict);
            em.merge(enrTheme) ;
            log.info("Assign Photo[{}] to Theme[{}]", pict, enrTheme.getNom());
        }
    }
}
