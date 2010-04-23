/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Photo;

/**
 *
 * @author kevin
 */
@Stateless
public class PhotoFacade implements PhotoFacadeLocal {
    @EJB WebAlbumsDAOLocal webDAO ;
    @EJB AlbumFacadeLocal albumDAO ;
    @PersistenceContext
    private EntityManager em;

    public void create(Photo photo) {
        em.persist(photo);
    }

    public void edit(Photo photo) {
        em.merge(photo);
    }

    public void remove(Photo photo) {
        em.remove(em.merge(photo));
    }

    public Photo find(Object id) {
        if (id == null) return null ;
        return em.find(Photo.class, id);
    }

    public List<Photo> findAll() {
        return em.createQuery("select object(o) from Photo as o")
                .getResultList();
    }

    public Photo loadIfAllowed(ServiceSession session, int id) {
            String rq = "FROM Photo p " +
            " WHERE p.id = :id " +
            (session == null ? "" : " AND "+webDAO.restrictToPhotosAllowed(session, "p"))+" " ;

        return (Photo) em.createQuery(rq)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Photo> loadFromAlbum(ServiceSession session, int albumId) {
          String rq = "FROM Photo p " +
            " WHERE p.album = :albumId " +
            (session == null ? "" : " AND "+webDAO.restrictToPhotosAllowed(session, "p"))+" " +
            " ORDER BY p.path" ;
          return em.createQuery(rq)
                  .setParameter("albumId", albumDAO.find(albumId))
                  .getResultList() ;

    }

    public Photo loadByPath(String path) {
            String rq = "FROM Photo p WHERE p.path = :path" ;
            return (Photo) em.createQuery(rq)
                    .setParameter("path", path)
                    .getSingleResult();
    }

    public List<Photo> loadByTags(ServiceSession session, List<Integer> listTagId) {
            //creation de la requete
            String rq = "SELECT p FROM Photo p, Album a, TagPhoto tp " +
              " WHERE a.id = p.album and p.id = tp.photo"+
              " AND tp.tag in ('-1' " ;
            for (int id : listTagId) {
              rq += ", '"+id+"'" ;
            }
            rq += ")" ;

            rq += " AND "+webDAO.restrictToPhotosAllowed(session, "p")+" " ;
            rq += " AND "+webDAO.restrictToThemeAllowed(session, "a")+" " ;
            rq += " GROUP BY p.id " ;
            rq += " ORDER BY p.path DESC " ;

            return em.createQuery(rq).getResultList()	;
    }
}
