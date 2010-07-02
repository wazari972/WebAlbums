/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.ArrayList;
import net.wazari.dao.*;
import java.util.List;
import java.util.logging.Logger;
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
    private static final Logger log = Logger.getLogger(ThemeFacade.class.getName());
     
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    public void remove(Theme theme) {
        em.remove(em.merge(theme));
    }

    @Override
    public List<Theme> findAll() {
        try {
            return em.createQuery("select object(o) from JPATheme as o").getResultList();
        } catch (javax.persistence.PersistenceException e) {
            log.warning("Database query failed ...");
            return new ArrayList<Theme>() ;
        }
    }

    @Override
    public JPATheme loadByName(String themeName) {
        try {
            String rq = "FROM JPATheme t WHERE t.nom = :nom";

          return (JPATheme) em.createQuery(rq)
                    .setParameter("nom", themeName)
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
                    .getSingleResult() ;
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public Theme newTheme(int id, String name) {
        Theme enrTheme = new JPATheme(id, name) ;
        em.merge(enrTheme) ;
        return enrTheme ;
    }

    @Override
    public Theme newTheme(String name) {
        Theme enrTheme = new JPATheme() ;
        enrTheme.setNom(name) ;
        em.merge(enrTheme) ;
        return enrTheme ;
    }
}
