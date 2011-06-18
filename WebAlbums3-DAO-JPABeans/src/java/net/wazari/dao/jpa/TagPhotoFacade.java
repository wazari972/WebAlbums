/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.Iterator;
import net.wazari.dao.entity.Carnet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;
import net.wazari.dao.jpa.entity.JPAAlbum;
import net.wazari.dao.jpa.entity.JPAAlbum_;
import net.wazari.dao.jpa.entity.JPAPhoto_;
import net.wazari.dao.jpa.entity.JPATag;
import net.wazari.dao.jpa.entity.JPATagPhoto;
import net.wazari.dao.jpa.entity.JPATagPhoto_;
import net.wazari.dao.jpa.entity.JPATag_;

/**
 *
 * @author kevin
 */
@Stateless
public class TagPhotoFacade implements TagPhotoFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(TagPhotoFacade.class.getName()) ;
    
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @EJB PhotoFacadeLocal photoDAO ;
    @EJB AlbumFacadeLocal albumDAO ;
    @EJB TagFacadeLocal   tagDAO ;
    @EJB ThemeFacadeLocal themeDAO ;

    @Override
    public void create(TagPhoto tagPhoto) {
        tagPhoto.getPhoto().getTagPhotoList().add(tagPhoto);
        tagPhoto.getTag().getTagPhotoList().add(tagPhoto);
        em.persist(tagPhoto);
    }

    @Override
    public void edit(TagPhoto tagPhoto) {
        em.merge(tagPhoto);
    }

    @Override
    public void remove(TagPhoto tagPhoto) {
        tagPhoto.getPhoto().getTagPhotoList().remove(tagPhoto);
        tagPhoto.getTag().getTagPhotoList().remove(tagPhoto);
        em.remove(tagPhoto);
    }

    @Override
    public void deleteByPhoto(Photo enrPhoto) {
        Iterator<TagPhoto> it = enrPhoto.getTagPhotoList().iterator() ;
        while (it.hasNext()) {
            TagPhoto enrTagPhoto = it.next() ;
            enrTagPhoto.getTag().getTagPhotoList().remove(enrTagPhoto);
            it.remove();
            em.remove(enrTagPhoto);
        }
    }

    @Override
    public List<TagPhoto> queryByAlbum(Album enrAlbum) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATagPhoto> cq = cb.createQuery(JPATagPhoto.class) ;
        Root<JPATagPhoto> tp = cq.from(JPATagPhoto.class);
        cq.where(cb.equal(tp.get(JPATagPhoto_.photo).get(JPAPhoto_.album), enrAlbum)) ;

        return (List) em.createQuery(cq.distinct(true))
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public TagPhoto loadByTagPhoto(int tagId, int photoId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATagPhoto> cq = cb.createQuery(JPATagPhoto.class) ;
        Root<JPATagPhoto> tp = cq.from(JPATagPhoto.class);
        cq.where(cb.and(
                cb.equal(tp.get(JPATagPhoto_.photo).get(JPAPhoto_.id), photoId)),
                cb.equal(tp.get(JPATagPhoto_.tag).get(JPATag_.id), tagId)) ;

        return (JPATagPhoto) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getSingleResult();
    }

    @Override
    public List<Tag> selectDistinctTags() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
        Root<JPATagPhoto> tp = cq.from(JPATagPhoto.class);
        return (List) em.createQuery(cq.select(tp.get(JPATagPhoto_.tag))
                .distinct(true))
                .getResultList();
    }

    @Override
    public List<Tag> selectUnusedTags(ServiceSession session) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATag> cq = cb.createQuery(JPATag.class) ;
        Root<JPAAlbum> a = cq.from(JPAAlbum.class);
        Root<JPATagPhoto> tp = cq.from(JPATagPhoto.class);

        cq.where(
                cb.equal(tp.get(JPATagPhoto_.photo).get(JPAPhoto_.album).get(JPAAlbum_.theme),
                   session.getTheme())) ;
        return (List) em.createQuery(cq.select(tp.get(JPATagPhoto_.tag)).distinct(true))
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public TagPhoto newTagPhoto() {
        return new JPATagPhoto() ;
    }

    @Override
    public List<TagPhoto> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<JPATagPhoto> cq = cb.createQuery(JPATagPhoto.class) ;
        cq.from(JPATagPhoto.class);
        return (List) em.createQuery(cq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public List<TagPhoto> queryByCarnet(Carnet carnet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
