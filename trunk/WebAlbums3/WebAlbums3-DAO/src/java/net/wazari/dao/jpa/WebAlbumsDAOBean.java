/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author kevin
 */
@Stateless
public class WebAlbumsDAOBean implements WebAlbumsDAOLocal {
    @PersistenceContext
    private EntityManager em;
    
    public String processListID(ServiceSession session, String rq, boolean restrict) {
        if (restrict && ! session.isRootSession()) {
            rq += " AND " + restrictToThemeAllowed(session, "a") + " ";
        }
        return rq;
    }

    public String restrictToAlbumsAllowed(ServiceSession session, String album) {
        String rq = null;
        if (session.isSessionManager()) {
            rq = "SELECT a.id FROM Album a WHERE 1 = 1 ";
        } else {
            rq = "SELECT a.id " +
                    "FROM Album a, Photo p " +
                    "WHERE a.id = p.album AND (" +
                    //albums autorisé
                    "((p.droit = 0 OR p.droit is null) AND a.droit >= '" + session.getUserId() + "') " +
                    "OR " +
                    //albums ayant au moins une photo autorisée
                    "(p.droit >= '" + session.getUserId() + "')" +
                    ") ";
        }
        rq = " " + album + ".id IN (" + processListID(session, rq, true) + ") ";
        return rq;
    }

    public String restrictToPhotosAllowed(ServiceSession session, String photo) {
        String rq = null;

        rq = "SELECT p.id " +
                " FROM Photo p, Album a " +
                " WHERE p.album = a.id ";
        if (!session.isSessionManager()) {
            rq += " AND (" +
                    //albums autorisé
                    "((p.droit = 0 OR p.droit is null) AND a.droit >= '" + session.getUserId() + "') " +
                    "OR " +
                    //albums ayant au moins une photo autorisée
                    "(p.droit >= '" + session.getUserId() + "')" +
                    ")";
        }

        rq = " " + photo + ".id IN (" + processListID(session, rq, true) + ") ";
        return rq;
    }

    public String restrictToThemeAllowed(ServiceSession session, String album) {
        if (session.isRootSession()) {
            return " 1 = 1";
        } else {
            return " " + album + ".theme = '" + session.getTheme().getId() + "' ";
        }
    }
}
