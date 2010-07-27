/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.List;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.wazari.dao.*;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.jpa.entity.JPATagTheme;

/**
 *
 * @author kevin
 */
@Stateless
public class TagThemeFacade implements TagThemeFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(TagThemeFacade.class.getName());

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

    private static Random rand = new Random() ;
    @Override
    public TagTheme loadByTagTheme(Integer tagId, Integer themeId) {
        if (tagId == null || themeId == null) return null ;

        try {
            if (themeId != ThemeFacadeLocal.THEME_ROOT_ID) {
                String rq = "FROM JPATagTheme tt " +
                        "WHERE tt.tag.id = :tagId " +
                        " AND tt.theme.id = :themeId ";
                return (TagTheme) em.createQuery(rq)
                        .setParameter("tagId", tagId)
                        .setParameter("themeId", themeId)
                        .setHint("org.hibernate.cacheable", true)
                        .setHint("org.hibernate.readOnly", false)
                        .getSingleResult();
            } else {
                String rq = "FROM JPATagTheme tt " +
                        "WHERE tt.tag.id = :tagId " ;

                List<TagTheme> lst = em.createQuery(rq)
                        .setParameter("tagId", tagId)
                        .setHint("org.hibernate.cacheable", true)
                        .setHint("org.hibernate.readOnly", false)
                        .getResultList();
                if (lst.isEmpty()) return null ;
                return lst.get(rand.nextInt(lst.size())) ;
            }
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public TagTheme newTagTheme() {
        return new JPATagTheme();
    }

    @Override
    public List<TagTheme> findAll() {
        String rq = "SELECT o FROM JPATagTheme o";
        return (List<TagTheme>) em.createQuery(rq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }
}
