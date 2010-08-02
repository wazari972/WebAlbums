/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.jpa.entity.JPATag;

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
        StringBuilder rq = new StringBuilder(80);
        rq.append("SELECT t, count( tp.photo ) AS count ")
          .append(" FROM JPATag t, JPATagPhoto tp, JPAPhoto p, JPAAlbum a ")
          .append(" WHERE t.id = tp.tag ")
          .append(" AND tp.photo = p.id ")
          .append(" AND p.album = a.id ")
          .append(" AND " )
          .append(webDAO.restrictToPhotosAllowed(session, "p") )
          .append(" AND ")
          .append(webDAO.restrictToThemeAllowed(session, "a"))
          .append(" ORDER BY t.nom ");
        List<Object[]> lst = em.createQuery(rq.toString())
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
        Map<Tag, Long> ret = new LinkedHashMap <Tag, Long>();
        for (Object[] current : lst) {
            ret.put((JPATag) current[0], (Long) current[1]);
        }
        return ret;
    }

    @Override
    public List<Tag> queryAllowedTagByType(ServiceSession session, int type) {
        StringBuilder rq = new StringBuilder(80);

        if (session.isRootSession()) {
            rq.append("FROM JPATag t WHERE t.tagType = :type") ;
        } else {
            rq.append("SELECT DISTINCT t ")
              .append("FROM JPATag t, JPATagPhoto tp, JPAPhoto p, JPAAlbum a ")
              .append("WHERE t.tagType = :type ")
              .append("AND t.id = tp.tag ")
              .append("AND tp.photo = p.id ")
              .append("AND p.album = a.id ")
              .append("AND ")
              .append(webDAO.restrictToPhotosAllowed(session, "p"))
              .append(" AND ")
              .append(webDAO.restrictToThemeAllowed(session, "a"));
        }
        rq.append(" ORDER BY t.nom ") ;
        return em.createQuery(rq.toString()).setParameter("type", type)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public Tag loadByName(String nom) {
            try {
            String rq = "FROM JPATag t WHERE t.nom = :nom ";

            return (JPATag) em.createQuery(rq).setParameter("nom", nom)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public List<Tag> loadVisibleTags(ServiceSession sSession, boolean restrictToGeo) {
        StringBuilder rq = new StringBuilder(80);
        rq.append("SELECT DISTINCT ta ")
              .append("FROM JPATag ta, JPATagPhoto tp, JPAPhoto p, JPAAlbum a ")
              .append("WHERE  ta.id = tp.tag AND tp.photo = p.id AND p.album = a.id ")
              .append("AND ")
              .append(webDAO.restrictToPhotosAllowed(sSession, "p"))
              .append("AND ")
              .append(webDAO.restrictToThemeAllowed(sSession, "a"));

        if (restrictToGeo) {
            rq.append(" AND ta.tagType = '3' ") ;
        }
        rq.append(" ORDER BY ta.nom");
        return em.createQuery(rq.toString())
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public List<Tag> getNoSuchTags(ServiceSession sSession, List<Tag> tags) {
        StringBuilder rq = new StringBuilder(80);
        rq.append("SELECT DISTINCT ta ")
              .append(" FROM JPATag ta ")
              .append(" WHERE ta.id NOT IN (")
              .append(getIdList(tags) )
              .append( ") " )
              .append(" ORDER BY ta.nom");
        return em.createQuery(rq.toString())
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    private static StringBuilder getIdList(List<Tag> lst) {
        StringBuilder rq = new StringBuilder(30);
        rq.append( "-1 ") ;
        for (Tag enrTag : lst) {
            rq.append(", ").append(enrTag.getId());
        }
        return rq ;
    }

    @Override
    public Tag find(Integer id) {
        try {
            String rq = "FROM JPATag t where t.id = :id";
            return  (JPATag) em.createQuery(rq)
                    .setParameter("id", id)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", false)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public List<Tag> findAll() {
        String rq = "FROM JPATag t";
        return em.createQuery(rq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList() ;
    }

    @Override
    public Tag newTag() {
        return new JPATag() ;
    }
}
