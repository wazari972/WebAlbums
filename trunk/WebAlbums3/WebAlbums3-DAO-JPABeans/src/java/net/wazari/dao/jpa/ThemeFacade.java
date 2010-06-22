/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.*;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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
     
    @PersistenceContext
    private EntityManager em;

    @Override
    public void create(Theme theme) {
        em.persist(theme);
    }

    @Override
    public void edit(Theme theme) {
        em.merge(theme);
    }

    @Override
    public void remove(Theme theme) {
        em.remove(em.merge(theme));
    }

    @Override
    public List<Theme> findAll() {
        return em.createQuery("select object(o) from JPATheme as o").getResultList();
    }

    @Override
    public JPATheme loadByName(String themeName) {
        String rq = "FROM JPATheme t WHERE t.nom = :nom";

        return (JPATheme) em.createQuery(rq)
                .setParameter("nom", themeName)
                .getSingleResult();
    }

    @Override
    public JPATheme find(Integer id) {
        String rq = "FROM JPATheme t WHERE t.id = :id";
        return (JPATheme) em.createQuery(rq)
                .setParameter("id", id)
                .getSingleResult() ;
    }

    @Override
    public Theme newTheme() {
        return new JPATheme() ;
    }
}
