/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.logging.Logger;
import net.wazari.dao.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.jpa.entity.JPATagTheme;

/**
 *
 * @author kevin
 */
@Stateless
public class TagThemeFacade implements TagThemeFacadeLocal {
    private static final Logger log = Logger.getLogger(TagThemeFacade.class.getName());

    @EJB TagFacadeLocal   tagDAO ;
    @EJB ThemeFacadeLocal themeDAO ;
    
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    public void create(TagTheme tagTheme) {
        em.persist(tagTheme);
    }

    @Override
    public void edit(TagTheme tagTheme) {
        em.merge(tagTheme);
    }

    @Override
    public void remove(TagTheme tagTheme) {
        em.remove(em.merge(tagTheme));
    }

    @Override
    public TagTheme loadByTagTheme(Integer tagId, Integer themeId) {
        if (tagId == null || themeId == null) return null ;
        try {
            String rq = "FROM JPATagTheme tt " +
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

    @Override
    public TagTheme newTagTheme() {
        return new JPATagTheme();
    }
}
