/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.List;
import java.util.Random;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagThemeFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.jpa.entity.JPATagTheme;
import net.wazari.dao.jpa.entity.JPATagTheme_;
import net.wazari.dao.jpa.entity.JPATag_;
import net.wazari.dao.jpa.entity.JPATheme_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public class TagThemeFacade implements TagThemeFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(TagThemeFacade.class.getName());

    @EJB TagFacadeLocal   tagDAO ;
    @EJB ThemeFacadeLocal themeDAO ;
    
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void create(TagTheme tagTheme) {
        em.persist(tagTheme);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void edit(TagTheme tagTheme) {
        em.merge(tagTheme);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void remove(TagTheme tagTheme) {
        em.remove(em.merge(tagTheme));
    }

    private static Random rand = new Random() ;
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public TagTheme loadByTagTheme(Integer tagId, Integer themeId) {
        if (tagId == null || themeId == null) return null ;

        try {
            if (themeId != ThemeFacadeLocal.THEME_ROOT_ID) {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<JPATagTheme> cq = cb.createQuery(JPATagTheme.class) ;
                Root<JPATagTheme> tt = cq.from(JPATagTheme.class);
                cq.where(cb.and(
                        cb.equal(tt.get(JPATagTheme_.tag).get(JPATag_.id), tagId),
                        cb.equal(tt.get(JPATagTheme_.theme).get(JPATheme_.id), themeId)
                        )) ;
                return (TagTheme) em.createQuery(cq)
                        .setHint("org.hibernate.cacheable", true)
                        .setHint("org.hibernate.readOnly", false)
                        .getSingleResult();
            } else {
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<JPATagTheme> cq = cb.createQuery(JPATagTheme.class) ;
                Root<JPATagTheme> tt = cq.from(JPATagTheme.class);
                cq.where(cb.equal(tt.get(JPATagTheme_.tag).get(JPATag_.id), tagId)) ;

                List<JPATagTheme> lst = em.createQuery(cq)
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
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public TagTheme newTagTheme() {
        return new JPATagTheme();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public List<TagTheme> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATagTheme> cq = cb.createQuery(JPATagTheme.class) ;
        cq.from(JPATagTheme.class);
        return (List) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }
}
