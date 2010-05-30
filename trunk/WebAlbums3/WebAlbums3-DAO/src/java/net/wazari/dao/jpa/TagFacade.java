/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import java.util.HashMap;
import net.wazari.dao.*;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Tag;

/**
 *
 * @author kevin
 */
@Stateless
public class TagFacade implements TagFacadeLocal {

    @EJB
    WebAlbumsDAOLocal webDAO;
    @PersistenceContext
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void create(Tag tag) {
        em.persist(tag);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void edit(Tag tag) {
        em.merge(tag);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void remove(Tag tag) {
        em.remove(em.merge(tag));
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Map<Tag, Long> queryIDNameCount(ServiceSession session) {
        String rq = "SELECT t, count( tp.photo ) AS count " +
                " FROM Tag t, TagPhoto tp, Photo p, Album a " +
                " WHERE t.id = tp.tag " +
                " AND tp.photo = p.id " +
                " AND p.album = a.id " +
                " AND " + webDAO.restrictToPhotosAllowed(session, "p") + " " +
                " AND " + webDAO.restrictToThemeAllowed(session, "a") + " " +
                " GROUP BY t.id ";
        List<Object[]> lst = em.createQuery(rq).getResultList();
        Map<Tag, Long> ret = new HashMap<Tag, Long>();
        for (Object[] current : lst) {
            ret.put((Tag) current[0], (Long) current[1]);
        }
        return ret;
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public List<Tag> queryAllowedTagByType(ServiceSession session, int type) {
        String rq;
        if (session.isRootSession()) {
            rq = "FROM Tag t WHERE t.tagType = :type";
        } else {
            rq = "SELECT DISTINCT t " +
                    "FROM Tag t, TagPhoto tp, Photo p, Album a " +
                    "WHERE t.tagType = :type " +
                    "AND t.id = tp.tag " +
                    "AND tp.photo = p.id " +
                    "AND p.album = a.id " +
                    "AND " + webDAO.restrictToPhotosAllowed(session, "p") + " " +
                    "AND " + webDAO.restrictToThemeAllowed(session, "a") + " ";
        }

        return em.createQuery(rq).setParameter("type", type).getResultList();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Tag loadByName(String nom) {
        String rq = "FROM Tag t " +
                " WHERE t.nom = :nom ";

        return (Tag) em.createQuery(rq).setParameter("nom", nom).getSingleResult();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public List<Tag> loadVisibleTags(ServiceSession sSession, boolean restrictToGeo) {
        String rq = "SELECT DISTINCT ta " +
                "FROM Tag ta, TagPhoto tp, Photo p, Album a " +
                "WHERE  ta.id = tp.tag AND tp.photo = p.id AND p.album = a.id " +
                "AND " + webDAO.restrictToPhotosAllowed(sSession, "p") + " " +
                "AND " + webDAO.restrictToThemeAllowed(sSession, "a") + " ";

        if (restrictToGeo) {
            rq += " AND ta.tagType = '3' ";
        }
        rq += " ORDER BY ta.nom";
        return em.createQuery(rq).getResultList();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public List<Tag> getNoSuchTags(ServiceSession sSession, List<Tag> tags) {
        String rq = "SELECT DISTINCT ta " +
                " FROM Tag ta " +
                " WHERE ta.id NOT IN (" + getIdList(tags) + ") " +
                " ORDER BY ta.nom";
        return em.createQuery(rq).getResultList();
    }

    private static String getIdList(List<Tag> lst) {
        String ret = "-1 ";
        for (Tag enrTag : lst) {
            ret += ", " + enrTag.getId();
        }
        return ret;
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public List<Tag> findAll(Integer tagId) {
        String rq = "FROM Tag t";
        return  em.createQuery(rq)
                .getResultList();
        
    }

    @Override
    public Tag find(Integer id) {
        try {
            String rq = "FROM Tag t where t.id = :id";
            return  (Tag) em.createQuery(rq)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public List<Tag> findAll() {
        String rq = "FROM Tag t";
        return em.createQuery(rq).getResultList() ;
    }
}
