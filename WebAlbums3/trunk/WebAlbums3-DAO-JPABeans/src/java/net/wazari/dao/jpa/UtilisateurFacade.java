/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import net.wazari.dao.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.jpa.entity.JPAUtilisateur;

/**
 *
 * @author kevin
 */
@Stateless
public class UtilisateurFacade implements UtilisateurFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(UtilisateurFacade.class.getCanonicalName()) ;

    static {
        log.warn("Loading WebAlbums3-DAO-JPABeans");
    }
    @EJB AlbumFacadeLocal albumDAO ;

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    public void newUser(int id, String nom) {
        em.merge(new JPAUtilisateur(id, nom));
    }

    @Override
    public Utilisateur loadByName(String name) {
        try {
            String rq = "FROM JPAUtilisateur u WHERE u.nom = :nom";
            return (JPAUtilisateur) em.createQuery(rq)
                    .setParameter("nom", name)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info ( "No user with name +{}+", name);
            return null ;
        }
    }

    @Override
    public Utilisateur loadUserOutside(int albumId) {
        String rq = "SELECT u FROM JPAUtilisateur u, JPAAlbum a WHERE u.id = a.droit AND a.id = :albumId";
        return (JPAUtilisateur) em.createQuery(rq)
                .setParameter("albumId", albumId)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getSingleResult();
    }

    @Override
    public List<Utilisateur> loadUserInside(int albumId) {
        StringBuilder rq = new StringBuilder(80);
        rq.append("SELECT DISTINCT u ")
            .append(" FROM JPAPhoto p, JPAUtilisateur u ")
            .append(" WHERE u.id = p.droit ")
            .append(" AND p.album.id = :albumId ")
            .append(" AND p.droit != null AND p.droit != 0");
        return em.createQuery(rq.toString())
            .setParameter("albumId", albumId)
            .setHint("org.hibernate.cacheable", true)
            .setHint("org.hibernate.readOnly", true)
            .getResultList();
    }

    @Override
    public JPAUtilisateur find(Integer droit) {
        try {
            String rq = "FROM JPAUtilisateur u WHERE u.id = :id";
            return (JPAUtilisateur) em.createQuery(rq)
                    .setParameter("id", droit)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        String rq = "FROM JPAUtilisateur u";
        return em.createQuery(rq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList() ;
    }
}
