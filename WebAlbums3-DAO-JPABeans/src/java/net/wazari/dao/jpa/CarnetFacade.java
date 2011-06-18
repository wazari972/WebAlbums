/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.List;
import net.wazari.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.jpa.entity.JPACarnet;
import net.wazari.dao.jpa.entity.JPACarnet_;

/**
 *
 * @author kevin
 */
@Stateless
public class CarnetFacade implements CarnetFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(CarnetFacade.class.getCanonicalName()) ;

    @EJB
    WebAlbumsDAOBean webDAO;

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @Override
    public void create(Carnet carnet) {
        em.persist(carnet);
    }

    @Override
    public void edit(Carnet carnet) {
        em.merge(carnet);
    }

    @Override
    public void remove(Carnet carnet) {
        em.remove(em.merge(carnet));
    }

    @Override
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
    public Carnet newCarnet() {
        return new JPACarnet();
    }

    @Override
    public List<Carnet> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPACarnet> cq = cb.createQuery(JPACarnet.class) ;
        cq.from(JPACarnet.class) ;
        return (List) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();

    }
}
