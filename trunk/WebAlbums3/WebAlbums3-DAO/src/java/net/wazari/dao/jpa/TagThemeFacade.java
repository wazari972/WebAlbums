/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    public void create(TagTheme tagTheme) {
        em.persist(tagTheme);
    }

    public void edit(TagTheme tagTheme) {
        em.merge(tagTheme);
    }

    public void remove(TagTheme tagTheme) {
        em.remove(em.merge(tagTheme));
    }

    public TagTheme find(Object id) {
        return em.find(TagTheme.class, id);
    }

    public List<TagTheme> findAll() {
        return em.createQuery("select object(o) from TagTheme as o").getResultList();
    }

    public List<TagTheme> queryByTag(ServiceSession session, int tagId) {
        String rq = "FROM TagTheme tt " +
                "WHERE tt.tag = :tagId " +
                (session.isRootSession() ? "" : " AND tt.theme = :themeId");
        Query q = em.createQuery(rq)
                .setParameter("tagId", tagDAO.find(tagId)) ;
        if (!session.isRootSession()) q.setParameter("themeId", themeDAO.find(session.getThemeId())) ;
        return q.getResultList();
    }

    public TagTheme loadByTagTheme(Integer tagId, Integer themeId) {
        try {
            String rq = "FROM TagTheme tt " +
                    "WHERE tt.tag = :tagId " +
                    " AND tt.theme = :themeId ";
            return (TagTheme) em.createQuery(rq)
                    .setParameter("tagId", tagDAO.find(tagId))
                    .setParameter("themeId", themeDAO.find(themeId))
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }
}
