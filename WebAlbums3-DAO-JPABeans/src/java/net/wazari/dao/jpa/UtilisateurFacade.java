/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.jpa.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE})
public class UtilisateurFacade implements UtilisateurFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(UtilisateurFacade.class.getCanonicalName()) ;

    static {
        log.warn("Loading WebAlbums3-DAO-JPABeans");
    }
    @EJB AlbumFacadeLocal albumDAO ;

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void newUser(int id, String nom) {
        em.merge(new JPAUtilisateur(id, nom));
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public Utilisateur loadByName(String name) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPAUtilisateur> cq = cb.createQuery(JPAUtilisateur.class) ;
            Root<JPAUtilisateur> u = cq.from(JPAUtilisateur.class);
            cq.where(cb.equal(u.get(JPAUtilisateur_.nom), name)) ;
            return (JPAUtilisateur) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            log.info ( "No user with name '{}'", name);
            return null ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public Utilisateur loadUserOutside(int albumId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPAUtilisateur> cq = cb.createQuery(JPAUtilisateur.class) ;
        Root<JPAAlbum> a = cq.from(JPAAlbum.class);
        cq.where(cb.and(
                cb.equal(a.get(JPAAlbum_.id), albumId))) ;
        return (JPAUtilisateur) em.createQuery(cq.select(a.get(JPAAlbum_.droit)))
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getSingleResult();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public List<Utilisateur> loadUserInside(int albumId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPAUtilisateur> cq = cb.createQuery(JPAUtilisateur.class) ;
        Root<JPAPhoto> p = cq.from(JPAPhoto.class);
        Root<JPAUtilisateur> u = cq.from(JPAUtilisateur.class);
        cq.where(cb.and(
                cb.equal(u.get(JPAUtilisateur_.id), p.get(JPAPhoto_.droit)),
                cb.equal(p.get(JPAPhoto_.album).get(JPAAlbum_.id), albumId),
                cb.isNotNull(p.get(JPAPhoto_.droit)),
                cb.notEqual(p.get(JPAPhoto_.droit), 0))) ;
        return (List) em.createQuery(cq.select(u).distinct(true))
            .setHint("org.hibernate.cacheable", true)
            .setHint("org.hibernate.readOnly", true)
            .getResultList();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public JPAUtilisateur find(Integer id) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPAUtilisateur> cq = cb.createQuery(JPAUtilisateur.class) ;
            Root<JPAUtilisateur> u = cq.from(JPAUtilisateur.class);
            cq.where(cb.equal(u.get(JPAUtilisateur_.id), id)) ;
            return (JPAUtilisateur) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public List<Utilisateur> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPAUtilisateur> cq = cb.createQuery(JPAUtilisateur.class) ;
        cq.from(JPAUtilisateur.class);
        return (List) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList() ;
    }
}
