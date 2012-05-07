/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.*;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.jpa.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
public class TagFacade implements TagFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(TagFacade.class.getName());
    
    @EJB
    WebAlbumsDAOBean webDAO;

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    public void create(Tag tag) {
        em.persist(tag);
    }

    @Override
    public void edit(Tag tag) {
        em.merge(tag);
    }

    @Override
    public void remove(Tag tag) {
        em.remove(tag);
    }

    @Override
    public Map<Tag, Long> queryIDNameCount(ServiceSession session) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        //FROM JPATag t, JPATagPhoto tp, JPAPhoto p, JPAAlbum a
        Root<JPAPhoto> fromPhoto = cq.from(JPAPhoto.class);
        Join<JPAPhoto, JPATagPhoto> fromTagPhoto = 
                fromPhoto.join(JPAPhoto_.jPATagPhotoList) ;
        Join<JPATagPhoto, JPATag> fromTag = fromTagPhoto.join(JPATagPhoto_.tag);
        
        cq.where(cb.and(webDAO.getRestrictionToCurrentTheme(session, 
                            fromPhoto.get(JPAPhoto_.album).get(JPAAlbum_.theme)), 
                        cb.isNull(fromTag.get(JPATag_.minor))));
        cq.groupBy(fromTag.get(JPATag_.id));
        
        TypedQuery<Object[]> tq = em.createQuery(
                cq.multiselect(
                    fromTag,
                    cb.count(fromTagPhoto)));
        List<Object[]> lst = tq.getResultList() ;
        Map<Tag, Long> ret = new LinkedHashMap <Tag, Long>();
        for (Object[] current : lst) {
            ret.put((JPATag) current[0], (Long) current[1]);
        }
        return ret;
    }

    @Override
    public List<Tag> queryAllowedTagByType(ServiceSession session, int type) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
        Root<JPATag> tag = cq.from(JPATag.class) ;
        if (session.isRootSession()) {
            cq.where(cb.and(cb.isNull(tag.get(JPATag_.minor)),
                            cb.equal(tag.get(JPATag_.tagType), type))) ;
        } else {
            ListJoin<JPATag, JPATagPhoto> tp = 
                    tag.join(JPATag_.jPATagPhotoList);
            Join<JPATagPhoto, JPAPhoto> p = tp.join(JPATagPhoto_.photo) ;
            cq.where(cb.and(
                    cb.isNull(tag.get(JPATag_.minor)),
                    cb.equal(tag.get(JPATag_.tagType), type),
                        webDAO.getRestrictionToCurrentTheme(session, 
                            p.get(JPAPhoto_.album).get(JPAAlbum_.theme)))) ;
        }
        cq.orderBy(cb.asc(tag.get(JPATag_.nom))) ;
        
        return (List) em.createQuery(cq.select(tag).distinct(true))
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public Tag loadByName(String nom) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
            Root<JPATag> tag = cq.from(JPATag.class);
            cq.where(cb.equal(tag.get(JPATag_.nom), nom)) ;
            return (JPATag) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public List<Tag> loadVisibleTags(ServiceSession sSession, boolean restrictToGeo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.conjunction() ;

        CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
        Root<JPATag> t = cq.from(JPATag.class);
        ListJoin<JPATag, JPATagPhoto> tp = t.join(JPATag_.jPATagPhotoList) ;
        Join<JPATagPhoto, JPAPhoto> p = tp.join(JPATagPhoto_.photo) ;
        cq.where(cb.and(
                webDAO.getRestrictionToCurrentTheme(sSession, 
                p.get(JPAPhoto_.album).get(JPAAlbum_.theme)),
                (restrictToGeo ? cb.equal(t.get(JPATag_.tagType), 3) : TRUE)
                )) ;

        cq.orderBy(cb.asc(t.get(JPATag_.nom))) ;
        
        return (List) em.createQuery(cq.select(t).distinct(true))
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public List<Tag> getNoSuchTags(ServiceSession sSession, List<Tag> tags) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
        Root<JPATag> tag = cq.from(JPATag.class);
        if (!tags.isEmpty())
            cq.where(tag.in(tags).not()) ;
        cq.orderBy(cb.asc(tag.get(JPATag_.nom))) ;
        
        return (List) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public Set<Tag> getChildren(Tag enrParent) {
        if (enrParent == null) throw new NullPointerException() ;
        Set<Tag> children = new HashSet<Tag>() ;
        for (Tag enrChild : enrParent.getSonList()) {
            children.add(enrChild) ;
            children.addAll(enrChild.getSonList());
        }
        return children ;
    }


    @Override
    public Tag find(Integer id) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
            Root<JPATag> tag = cq.from(JPATag.class);
            cq.where(cb.equal(tag.get(JPATag_.id), id)) ;
            return  (JPATag) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", false)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public List<Tag> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
        cq.from(JPATag.class);
        return (List) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList() ;
    }

    @Override
    public Tag newTag() {
        return new JPATag() ;
    }
}
