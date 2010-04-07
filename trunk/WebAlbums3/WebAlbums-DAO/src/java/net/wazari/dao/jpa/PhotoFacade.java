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
        return em.find(Photo.class, id);
    }

    public List<Photo> findAll() {
        return em.createQuery("select object(o) from Photo as o").getResultList();
    }

    public Photo loadIfAllowed(ServiceSession session, int id) {
            String rq = "FROM Photo p " +
            " WHERE p.ID = "+id+" " +
            (session == null ? "" : " AND "+webDAO.restrictToPhotosAllowed(session, "p"))+" " ;

        return (Photo) em.createQuery(rq).getResultList();
    }

    public List<Photo> loadFromAlbum(ServiceSession session, int albumID) {
          String rq = "FROM Photo p " +
            " WHERE p.Album = '"+albumID+"' " +
            (session == null ? "" : " AND "+webDAO.restrictToPhotosAllowed(session, "p"))+" " +
            " ORDER BY path" ;
          return em.createQuery(rq).getResultList() ;

    }

    public Photo loadByPath(String path) {
            String rq = "FROM Photo WHERE Path = '"+path+"'" ;
            return (Photo) em.createQuery(rq).getResultList();
    }

    public List<Photo> loadByTags(ServiceSession session, List<Integer> listTagId) {
            //creation de la requete
            String rq = "SELECT p FROM Photo p, Album a, TagPhoto tp " +
              " WHERE a.ID = p.Album and p.ID = tp.Photo"+
              " AND tp.Tag in ('-1' " ;
            for (int id : listTagId) {
              rq += ", '"+id+"'" ;
            }
            rq += ")" ;

            rq += " AND "+webDAO.restrictToPhotosAllowed(session, "p")+" " ;
            rq += " AND "+webDAO.restrictToThemeAllowed(session, "a")+" " ;
            rq += " GROUP BY p.ID " ;
            rq += " ORDER BY p.Path DESC " ;

            return em.createQuery(rq).getResultList()	;
    }
}
