/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.jpa.entity.JPATheme;
import net.wazari.dao.jpa.entity.JPATheme_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE})
public class ThemeFacade implements ThemeFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(ThemeFacade.class.getName());
     
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void edit(Theme enrTheme) {
        em.merge(enrTheme);
    }
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void remove(Theme theme, boolean protect) {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT != WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod) {
            throw new IllegalStateException("cannot remove a theme while ProtectedDB is enabled") ;
        }
        em.remove(em.merge(theme));
    }

    @Override
    @PermitAll
    public List<Theme> findAll() {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPATheme> cq = cb.createQuery(JPATheme.class);
            cq.from(JPATheme.class);
            return (List) em.createQuery(cq)
                                .getResultList();
        } catch (javax.persistence.PersistenceException e) {
            log.warn("Database query failed ...");
            return new ArrayList<Theme>() ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public JPATheme loadByName(String themeName) {
        try {
            //FROM JPATheme t WHERE t.nom = :nom
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPATheme> cq = cb.createQuery(JPATheme.class);
            Root<JPATheme> from = cq.from(JPATheme.class);
            CriteriaQuery<JPATheme> select = cq.select(from);
            cq.where(cb.equal(from.get(JPATheme_.nom), themeName)) ;
            TypedQuery<JPATheme> tq = em.createQuery(select);
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    @PermitAll
    public JPATheme find(Integer id) {
        try {
            //FROM JPATheme t WHERE t.id = :id
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPATheme> cq = cb.createQuery(JPATheme.class);
            Root<JPATheme> from = cq.from(JPATheme.class);
            CriteriaQuery<JPATheme> select = cq.select(from);
            cq.where(cb.equal(from.get(JPATheme_.id), id)) ;
            TypedQuery<JPATheme> tq = em.createQuery(select);
            return tq.getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void preconfigureDatabase() {
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate();
        em.createNativeQuery("ALTER TABLE `Album` CHANGE `ID` `ID` INT(11) NOT NULL AUTO_INCREMENT ;").executeUpdate();
        em.createNativeQuery("ALTER TABLE `Photo` CHANGE `ID` `ID` INT(11) NOT NULL AUTO_INCREMENT ;").executeUpdate();
        em.createNativeQuery("ALTER TABLE `Tag`   CHANGE `ID` `ID` INT(11) NOT NULL AUTO_INCREMENT ;").executeUpdate();
        em.createNativeQuery("ALTER TABLE `Theme` CHANGE `ID` `ID` INT(11) NOT NULL AUTO_INCREMENT ;").executeUpdate();
        em.createNativeQuery("ALTER TABLE `Carnet` CHANGE `ID` `ID` INT(11) NOT NULL AUTO_INCREMENT ;").executeUpdate();
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS=0;").executeUpdate();    
    }
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public Theme newTheme(int id, String name) {
        Theme enrTheme = new JPATheme(id, name) ;
        
        return em.merge(enrTheme) ;
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public Theme newTheme(String name) {
        Theme enrTheme = new JPATheme() ;
        enrTheme.setNom(name) ;
        
        return em.merge(enrTheme) ;
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void setBackground(Theme enrTheme, Photo enrPhoto) {
        if (enrTheme != null) {
            enrTheme.setBackground(enrPhoto);
            em.merge(enrTheme) ;
            log.info("Assign Photo[{}] to Theme.Background[{}]", enrPhoto, enrTheme.getNom());
        }
    }
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void setPicture(Theme enrTheme, Photo enrPhoto) {
        if (enrTheme != null) {
            enrTheme.setPicture(enrPhoto);
            em.merge(enrTheme) ;
            log.info("Assign Photo[{}] to Theme.Picture[{}]", enrPhoto, enrTheme.getNom());
        }
    }
}
