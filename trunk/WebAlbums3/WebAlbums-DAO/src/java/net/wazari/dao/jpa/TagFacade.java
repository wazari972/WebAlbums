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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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

    public void create(Tag tag) {
        em.persist(tag);
    }

    public void edit(Tag tag) {
        em.merge(tag);
    }

    public void remove(Tag tag) {
        em.remove(em.merge(tag));
    }

    public Tag find(Object id) {
        return em.find(Tag.class, id);
    }

    public List<Tag> findAll() {
        return em.createQuery("select object(o) from Tag as o").getResultList();
    }

    /*
    public long getMsaxTagPerPhoto(ServiceDaoSession session) {
    String rq = queryIDNameCount(session).getQueryString();

    String rqMax = "SELECT max( count ) " +
    "FROM ( " +
    rq +
    ")temp";
    Query query = em.createSQLQuery(rqMax);
    rqMax = "done";

    Object val = query.getResultList();
    long max = 100;
    if (val != null) {
    max = Long.valueOf(val.toString());
    }
    return max;
    }
     */
    public long getMaxTagPerPhoto(ServiceSession session) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<Tag, Long> queryIDNameCount(ServiceSession session) {
        String rq = "SELECT t, count( tp.Photo ) AS count " +
                " FROM Tag t, TagPhoto tp, Photo p, Album a " +
                " WHERE t.ID = tp.Tag " +
                " AND tp.Photo = p.ID " +
                " AND p.Album = a.ID " +
                " AND " + webDAO.restrictToPhotosAllowed(session, "p") + " " +
                " AND " + webDAO.restrictToThemeAllowed(session, "a") + " " +
                " GROUP BY t.ID ";
        List<Object[]> lst = em.createQuery(rq).getResultList();
        Map<Tag, Long> ret = new HashMap<Tag, Long>();
        for (Object[] current : lst) {
            ret.put((Tag) current[0], (Long) current[1]);
        }
        return ret;
    }

    public List<Tag> queryAllowedTagByType(ServiceSession session, int type) {
        String rq;
        if (session.isRootSession()) {
            rq = "FROM Tag t WHERE t.TagType = '" + type + "'";
        } else {
            rq = "SELECT DISTINCT t " +
                    "FROM Tag t, TagPhoto tp, Photo p, Album a " +
                    "WHERE t.TagType = '" + type + "' " +
                    "AND t.ID = tp.Tag " +
                    "AND tp.Photo = p.ID " +
                    "AND p.Album = a.ID " +
                    "AND " + webDAO.restrictToPhotosAllowed(session, "p") + " " +
                    "AND " + webDAO.restrictToThemeAllowed(session, "a") + " ";
        }

        return em.createQuery(rq).getResultList();
    }

    public Tag loadByName(String nom) {
        String rq = "FROM Tag t " +
                " WHERE  t.Nom = '" + nom + "' ";

        return (Tag) em.createQuery(rq).getSingleResult();
    }

    public List<Tag> loadVisibleTags(ServiceSession sSession, boolean restrictToGeo) {
        String rq = "SELECT DISTINCT ta " +
                "FROM Tag ta, TagPhoto tp, Photo p, Album a " +
                "WHERE  ta.ID = tp.Tag AND tp.Photo = p.ID AND p.Album = a.ID " +
                "AND " + webDAO.restrictToPhotosAllowed(sSession, "p") + " " +
                "AND " + webDAO.restrictToThemeAllowed(sSession, "a") + " ";

        if (restrictToGeo) {
            rq += " AND ta.TagType = '3' ";
        }
        rq += " ORDER BY ta.Nom";
        return em.createQuery(rq).getResultList() ;
    }

    public List<Tag> getNoSuchTags(ServiceSession sSession, List<Tag> tags) {
        String rq = "SELECT DISTINCT ta " +
                " FROM Tag ta " +
                " WHERE ta.id NOT IN (" + getIdList(tags)+ ") " +
                " ORDER BY ta.Nom";
        return em.createQuery(rq).getResultList();
    }

    private static String getIdList(List<Tag> lst) {
        String ret = "-1 " ;
        for (Tag enrTag : lst) ret += ", "+enrTag.getId() ;
        return ret ;
    }
    
}
