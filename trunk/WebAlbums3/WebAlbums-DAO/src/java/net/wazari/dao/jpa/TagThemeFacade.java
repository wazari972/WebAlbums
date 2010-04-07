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

    public List<TagTheme> queryByTag(ServiceSession session, int tag) {
        String rq = "FROM TagTheme " +
                "WHERE tag = " + tag + "" +
                (session.isRootSession() ? "" : " AND theme = " + session.getThemeId());
        return em.createQuery(rq).getResultList();
    }

    public TagTheme loadByTagTheme(int tagID, int themeID) {
        String rq = "FROM TagTheme " +
                "WHERE tag = " + tagID + " " +
                " AND theme = " + themeID + " ";
        return (TagTheme) em.createQuery(rq).getSingleResult();
    }
}
