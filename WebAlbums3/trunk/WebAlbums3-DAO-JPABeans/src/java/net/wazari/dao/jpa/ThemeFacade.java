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
import net.wazari.dao.entity.Theme;
import net.wazari.dao.jpa.entity.JPATheme;

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
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT != WebAlbumsDAOBean.PERSISTENCE_UNIT_MySQL) {
            throw new IllegalStateException("cannot remove a theme while ProtectedDB is enabled") ;
        }
        em.remove(em.merge(theme));
    }

    @Override
    public List<Theme> findAll() {
        try {
            return em.createQuery("select object(o) from JPATheme as o").getResultList();
        } catch (javax.persistence.PersistenceException e) {
            log.warn("Database query failed ...");
            return new ArrayList<Theme>() ;
        }
    }

    @Override
    public JPATheme loadByName(String themeName) {
        try {
            String rq = "FROM JPATheme t WHERE t.nom = :nom";

          return (JPATheme) em.createQuery(rq)
                    .setParameter("nom", themeName)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public JPATheme find(Integer id) {
        try {
            String rq = "FROM JPATheme t WHERE t.id = :id";
            return (JPATheme) em.createQuery(rq)
                    .setParameter("id", id)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult() ;
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
        }
    }
}
