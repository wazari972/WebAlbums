/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.GpxFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Gpx;
import net.wazari.dao.jpa.entity.JPAGpx;
import net.wazari.dao.jpa.entity.JPAGpx_;

/**
 *
 * @author kevin
 */
@Stateless
public class GpxFacade implements GpxFacadeLocal {
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;
    
    @Override
    public Gpx find(Integer id) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPAGpx> cq = cb.createQuery(JPAGpx.class) ;
            Root<JPAGpx> g = cq.from(JPAGpx.class);
            cq.where(cb.equal(g.get(JPAGpx_.id), id)) ;
            return (JPAGpx) em.createQuery(cq)
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }
    
    @Override
    public void create(Gpx gpx) {
        em.persist(gpx);
    }

    @Override
    public void edit(Gpx gpx) {
        em.merge(gpx);
    }

    @Override
    public void remove(Gpx gpx) {
        em.remove(em.merge(gpx));
    }

    @Override
    public Gpx newGpx(Album enrAlbum) {
        JPAGpx g = new JPAGpx();
        g.setAlbum(enrAlbum);
        return g;
    }
    
    @Override
    public Gpx loadByPath(String path) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<JPAGpx> cq = cb.createQuery(JPAGpx.class) ;
            Root<JPAGpx> p = cq.from(JPAGpx.class);
            cq.where(cb.equal(p.get(JPAGpx_.gpxPath), path)) ;
            return (JPAGpx) em.createQuery(cq)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null ;
        }
    }
}
