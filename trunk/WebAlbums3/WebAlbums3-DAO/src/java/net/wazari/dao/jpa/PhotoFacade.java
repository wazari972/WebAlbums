/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import net.wazari.dao.entity.Photo;

/**
 *
 * @author kevin
 */
@Stateless
public class PhotoFacade implements PhotoFacadeLocal {

    @EJB
    WebAlbumsDAOLocal webDAO;
    @EJB
    AlbumFacadeLocal albumDAO;
    @PersistenceContext
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void create(Photo photo) {
        em.persist(photo);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void edit(Photo photo) {
        em.merge(photo);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void remove(Photo photo) {
        em.remove(em.merge(photo));
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Photo loadIfAllowed(ServiceSession session, int id) {
        try {
            String rq = "FROM Photo p " +
                    " WHERE p.id = :id " +
                    (session == null ? "" : " AND " + webDAO.restrictToPhotosAllowed(session, "p")) + " ";

            return (Photo) em.createQuery(rq).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public List<Photo> loadFromAlbum(ServiceSession session, int albumId, Integer first) {
        String rq = "FROM Photo p " +
                " WHERE p.album.id = :albumId " +
                (session == null ? "" : " AND " + webDAO.restrictToPhotosAllowed(session, "p")) + " " +
                " ORDER BY p.path";
        Query q = em.createQuery(rq).setParameter("albumId", albumId);
        if (first != null) {
            q.setFirstResult(first);
            q.setMaxResults(session.getPhotoSize());
        }

        return q.getResultList();

    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Photo loadByPath(String path) {
        String rq = "FROM Photo p WHERE p.path = :path";
        return (Photo) em.createQuery(rq).setParameter("path", path).getSingleResult();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public List<Photo> loadByTags(ServiceSession session, List<Integer> listTagId, Integer first) {
        //creation de la requete
        String rq = "SELECT p FROM Photo p, Album a, TagPhoto tp " +
                " WHERE a.id = p.album and p.id = tp.photo" +
                " AND tp.tag in ('-1' ";
        for (int id : listTagId) {
            rq += ", '" + id + "'";
        }
        rq += ")";

        rq += " AND " + webDAO.restrictToPhotosAllowed(session, "p") + " ";
        rq += " AND " + webDAO.restrictToThemeAllowed(session, "a") + " ";
        rq += " GROUP BY p.id ";
        rq += " ORDER BY p.path DESC ";

        Query q = em.createQuery(rq);
        if (first != null) {
            q.setFirstResult(first);
            q.setMaxResults(session.getPhotoSize());
        }
        return q.getResultList();
    }

    @Override
    public Photo find(Integer id) {
        try {
            String rq = "FROM Photo p WHERE p.id = :id";
            return (Photo) em.createQuery(rq).setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }
}
