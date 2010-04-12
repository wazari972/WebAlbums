/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.TagTheme;

/**
 *
 * @author kevin
 */
@Stateless
public class TagThemeFacade implements TagThemeFacadeLocal {

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
        String rq = "FROM TagTheme " +
                "WHERE tag = :tagId " +
                (session.isRootSession() ? "" : " AND theme = :themeId");
        return em.createQuery(rq)
                .setParameter("tagId", tagId)
                .setParameter("themeId", session.getUserId())
                .getResultList();
    }

    public TagTheme loadByTagTheme(int tagId, int themeId) {
        String rq = "FROM TagTheme " +
                "WHERE tag = :tagId " +
                " AND theme = :themeId ";
        return (TagTheme) em.createQuery(rq)
                .setParameter("tagId", tagId)
                .setParameter("themeId", themeId)
                .getSingleResult();
    }
}
