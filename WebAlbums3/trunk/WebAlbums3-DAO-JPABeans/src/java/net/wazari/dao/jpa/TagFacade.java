/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.HashSet;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.jpa.entity.JPAAlbum;
import net.wazari.dao.jpa.entity.JPAAlbum_;
import net.wazari.dao.jpa.entity.JPAPhoto;
import net.wazari.dao.jpa.entity.JPAPhoto_;
import net.wazari.dao.jpa.entity.JPATag;
import net.wazari.dao.jpa.entity.JPATagPhoto;
import net.wazari.dao.jpa.entity.JPATagPhoto_;
import net.wazari.dao.jpa.entity.JPATag_;

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
        Root<JPATag> fromTag = cq.from(JPATag.class);
        Root<JPATagPhoto> fromTagPhoto = cq.from(JPATagPhoto.class);
        Root<JPAPhoto> fromPhoto = cq.from(JPAPhoto.class);
        Root<JPAAlbum> fromAlbum = cq.from(JPAAlbum.class);
        cq.where(cb.and(
                cb.equal(fromPhoto.get(JPAPhoto_.id), fromTagPhoto.get(JPATagPhoto_.photo)),
                cb.equal(fromTagPhoto.get(JPATagPhoto_.tag), fromTag.get(JPATag_.id)),
                webDAO.getRestrictionToPhotosAllowed(session, fromPhoto, cq.subquery(JPAPhoto.class))),
                webDAO.getRestrictionToCurrentTheme(session, fromAlbum, AlbumFacadeLocal.Restriction.ALLOWED_AND_THEME)) ;
        //WHERE t.id = tp.tag
        // AND tp.photo = p.id
        // AND p.album = a.id
        // AND
        //webDAO.restrictToPhotosAllowed(session, "p")
        // AND
        //webDAO.restrictToThemeAllowed(session, "a")
        // GROUP BY t.id
        cq.groupBy(fromTag.get(JPATag_.id));
        // ORDER BY t.nom
        
        //SELECT t, count( tp.photo ) AS count
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
        StringBuilder rq = new StringBuilder(80);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
        Root<JPATag> tag = cq.from(JPATag.class) ;
        if (session.isRootSession()) {
            cq.where(cb.equal(tag.get(JPATag_.tagType), type)) ;
        } else {
            Root<JPATagPhoto> tp = cq.from(JPATagPhoto.class) ;
            Root<JPAPhoto> p = cq.from(JPAPhoto.class) ;
            Root<JPAAlbum> a = cq.from(JPAAlbum.class) ;
             cq.where(cb.and(
                cb.equal(tp.get(JPATagPhoto_.photo), p.get(JPAPhoto_.id)),
                cb.equal(p.get(JPAPhoto_.album), a.get(JPAAlbum_.id)),
                webDAO.getRestrictionToAlbumsAllowed(session, a, cq.subquery(JPAAlbum.class), Restriction.ALLOWED_AND_THEME),
                webDAO.getRestrictionToCurrentTheme(session, a, Restriction.ALLOWED_AND_THEME))) ;
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
            cq.where(cb.equal(tag.get(JPATag_.id), nom)) ;
            return (JPATag) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public List<Tag> loadVisibleTags(ServiceSession sSession, boolean restrictToGeo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate TRUE = cb.conjunction() ;

        CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
        Root<JPATag> ta = cq.from(JPATag.class);
        Root<JPATagPhoto> tp = cq.from(JPATagPhoto.class);
        Root<JPAPhoto> p = cq.from(JPAPhoto.class);
        Root<JPAAlbum> a = cq.from(JPAAlbum.class);
        cq.where(cb.and(
                cb.equal(ta.get(JPATag_.id), tp.get(JPATagPhoto_.tag)),
                cb.equal(tp.get(JPATagPhoto_.photo), p.get(JPAPhoto_.id)),
                cb.equal(p.get(JPAPhoto_.album), a.get(JPAAlbum_.id)),
                webDAO.getRestrictionToAlbumsAllowed(sSession, a, cq.subquery(JPAAlbum.class), Restriction.ALLOWED_AND_THEME),
                webDAO.getRestrictionToCurrentTheme(sSession, a, Restriction.ALLOWED_AND_THEME),
                (restrictToGeo ? cb.equal(ta.get(JPATag_.tagType), 3) : TRUE)
                )) ;

        cq.orderBy(cb.asc(ta.get(JPATag_.nom))) ;
        
        return (List) em.createQuery(cq.select(ta))
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public List<Tag> getNoSuchTags(ServiceSession sSession, List<Tag> tags) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
        Root<JPATag> tag = cq.from(JPATag.class);
        cq.where(tag.get(JPATag_.id).in(tags).not()) ;
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
        Root<JPATag> tag = cq.from(JPATag.class);
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
