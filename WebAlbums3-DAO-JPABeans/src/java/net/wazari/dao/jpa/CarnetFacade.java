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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.AlbumFacadeLocal.TopFirst;
import net.wazari.dao.CarnetFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.jpa.entity.JPACarnet;
import net.wazari.dao.jpa.entity.JPACarnet_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public class CarnetFacade implements CarnetFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(CarnetFacade.class.getCanonicalName()) ;

    @EJB
    WebAlbumsDAOBean webDAO;

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void create(Carnet carnet) {
        em.persist(carnet);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void edit(Carnet carnet) {
        em.merge(carnet);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void remove(Carnet carnet) {
        em.remove(carnet);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public Carnet find(Integer id) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPACarnet> cq = cb.createQuery(JPACarnet.class) ;
            Root<JPACarnet> carnet = cq.from(JPACarnet.class);
            cq.where(cb.equal(carnet.get(JPACarnet_.id), id)) ;
            return (JPACarnet) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public Carnet newCarnet() {
        return new JPACarnet();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public List<Carnet> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPACarnet> cq = cb.createQuery(JPACarnet.class) ;
        cq.from(JPACarnet.class) ;
        return (List) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();

    }
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public SubsetOf<Carnet> queryCarnets(ServiceSession session,
            AlbumFacadeLocal.Restriction restrict, AlbumFacadeLocal.TopFirst topFirst, Bornes bornes) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPACarnet> cq = cb.createQuery(JPACarnet.class) ;

        Root<JPACarnet> carnet = cq.from(JPACarnet.class);
        cq.where(webDAO.getRestrictionToCurrentTheme(session, carnet.get(JPACarnet_.theme), restrict)) ;
        cq.orderBy(cb.desc(carnet.get(JPACarnet_.date))) ;
        TypedQuery<JPACarnet> q = em.createQuery(cq);

        int size = q.getResultList().size() ;
        if (topFirst == TopFirst.TOP) {
            q.setFirstResult(0);
            q.setMaxResults(bornes.getNbElement());
        } else if (topFirst == TopFirst.FIRST) {
            q.setFirstResult(bornes.getFirstElement());
            q.setMaxResults(session.getPhotoAlbumSize());
        }
        q.setHint("org.hibernate.cacheable", true) ;
        q.setHint("org.hibernate.readOnly", true) ;

        List<JPACarnet> lstCarnets = q.getResultList() ;
        return (SubsetOf) new SubsetOf<JPACarnet>(bornes, lstCarnets, (long) size);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public Carnet loadIfAllowed(ServiceSession session, Integer carnetId) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPACarnet> cq = cb.createQuery(JPACarnet.class) ;
            Root<JPACarnet> c = cq.from(JPACarnet.class);
            cq.where(cb.equal(c.get(JPACarnet_.id), carnetId)) ;
            return (JPACarnet) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .setHint("org.hibernate.readOnly", false)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }
}
