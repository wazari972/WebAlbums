/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Album;

/**
 *
 * @author kevin
 */
@Stateless
public class AlbumFacade implements AlbumFacadeLocal {
    private static final Logger log = Logger.getLogger(AlbumFacade.class.getCanonicalName()) ;

    @EJB
    WebAlbumsDAOLocal webDAO;
    @PersistenceContext
    private EntityManager em;

    public void create(Album album) {
        em.persist(album);
    }

    public void edit(Album album) {
        em.merge(album);
    }

    public void remove(Album album) {
        em.remove(em.merge(album));
    }

    public Album find(Object id) {
        return em.find(Album.class, id);
    }

    public List<Album> findAll() {
        return em.createQuery("select object(o) from Album as o").getResultList();
    }

    public List<Album> queryAlbums(ServiceSession session, boolean restrictAllowed, boolean restrictTheme, Integer topX) {
        String rq = (topX == null ? "" : "TOP " + topX + " ") +
                "FROM Album a " +
                " WHERE " + (restrictAllowed ? webDAO.restrictToAlbumsAllowed(session, "a") : "1 = 1") + " " +
                " AND " + (restrictTheme ? webDAO.restrictToThemeAllowed(session, "a") : "1 = 1") + " " +
                " ORDER BY a.date DESC ";
        log.info(rq);
        return em.createQuery(rq).getResultList();
    }

    public Album loadIfAllowed(ServiceSession session, int id) {
        String rq = "FROM Album a " +
                " WHERE " + webDAO.restrictToAlbumsAllowed(session, "a") + " " +
                " AND a.id = :id ";

        return (Album) em.createQuery(rq).setParameter("id", id).getSingleResult();
    }

    public Album loadByNameDate(String name, String date) {
        String rq = "FROM Album a " +
                " WHERE a.date = :date " +
                " AND a.nom = :nom";
        return (Album) em.createQuery(rq).setParameter("date", date).setParameter("nom", name).getSingleResult();
    }

    public void setDateStr(Album enrAlbum, String date) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
