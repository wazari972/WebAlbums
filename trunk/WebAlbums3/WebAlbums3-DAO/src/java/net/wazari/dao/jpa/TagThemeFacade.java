/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.*;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.TagTheme;

/**
 *
 * @author kevin
 */
@Stateless
public class TagThemeFacade implements TagThemeFacadeLocal {
    @EJB TagFacadeLocal   tagDAO ;
    @EJB ThemeFacadeLocal themeDAO ;
    @PersistenceContext
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void create(TagTheme tagTheme) {
        em.persist(tagTheme);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void edit(TagTheme tagTheme) {
        em.merge(tagTheme);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void remove(TagTheme tagTheme) {
        em.remove(em.merge(tagTheme));
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public TagTheme loadByTagTheme(Integer tagId, Integer themeId) {
        if (tagId == null || themeId == null) return null ;
        try {
            String rq = "FROM TagTheme tt " +
                    "WHERE tt.tag.id = :tagId " +
                    " AND tt.theme.id = :themeId ";
            return (TagTheme) em.createQuery(rq)
                    .setParameter("tagId", tagId)
                    .setParameter("themeId", themeId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }
}
