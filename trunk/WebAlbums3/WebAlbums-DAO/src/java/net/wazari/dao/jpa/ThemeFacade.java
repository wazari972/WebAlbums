/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.*;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Theme;

/**
 *
 * @author kevin
 */
@Stateless
public class ThemeFacade implements ThemeFacadeLocal {

    @PersistenceContext
    private EntityManager em;

    public void create(Theme theme) {
        em.persist(theme);
    }

    public void edit(Theme theme) {
        em.merge(theme);
    }

    public void remove(Theme theme) {
        em.remove(em.merge(theme));
    }

    public Theme find(Object id) {
        return em.find(Theme.class, id);
    }

    public List<Theme> findAll() {
        return em.createQuery("select object(o) from Theme as o").getResultList();
    }

    public Theme loadByName(String themeName) {
        String rq = "FROM Theme WHERE nom = :nom";

        return (Theme) em.createQuery(rq)
                .setParameter("nom", themeName)
                .getSingleResult();
    }
}
