/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.*;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void create(Theme theme) {
        em.persist(theme);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void edit(Theme theme) {
        em.merge(theme);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void remove(Theme theme) {
        em.remove(em.merge(theme));
    }

    @Override
    @PermitAll
    public List<Theme> findAll() {
        return em.createQuery("select object(o) from Theme as o").getResultList();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Theme loadByName(String themeName) {
        String rq = "FROM Theme t WHERE t.nom = :nom";

        return (Theme) em.createQuery(rq)
                .setParameter("nom", themeName)
                .getSingleResult();
    }

    @Override
    public Theme find(Integer id) {
        String rq = "FROM Theme t WHERE t.id = :id";
        return (Theme) em.createQuery(rq)
                .setParameter("id", id)
                .getSingleResult() ;
    }
}
