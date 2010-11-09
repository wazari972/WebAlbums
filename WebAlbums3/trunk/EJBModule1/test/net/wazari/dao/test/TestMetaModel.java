/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.test;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import junit.framework.Assert;
import net.wazari.dao.jpa.entity.JPAAlbum;
import net.wazari.dao.jpa.entity.metamodel.JPAAlbum_;
import net.wazari.dao.jpa.entity.metamodel.JPATheme_;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kevin
 */
public class TestMetaModel {

    private EntityManagerFactory emf;
    protected EntityManager em;

    @Before
    public void init() {
        emf = Persistence.createEntityManagerFactory("WebAlbums-MySQL-StandAlone");
        em = emf.createEntityManager();
    }

    @Test
    public void bar() {
        StringBuilder rq = new StringBuilder(80);
        rq.append("FROM ").append(JPAAlbum.class.getName());

        Query q = em.createQuery(rq.toString()).setFirstResult(0).setMaxResults(1).setHint("org.hibernate.cacheable", true).setHint("org.hibernate.readOnly", true);

        q.getSingleResult();
    }
    
    @Test
    public void foo() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<JPAAlbum> query = builder.createQuery(JPAAlbum.class);

        Root<JPAAlbum> album = query.from(JPAAlbum.class);
        Assert.assertNotNull(JPAAlbum_.theme); // no problem here
        Assert.assertNotNull(album.get(JPAAlbum_.theme)); // no problem here

        query.where(builder.equal(album.get(JPAAlbum_.theme).get(JPATheme_.id), 1L));

        List<JPAAlbum> results = em.createQuery(query).getResultList();
    }
}
