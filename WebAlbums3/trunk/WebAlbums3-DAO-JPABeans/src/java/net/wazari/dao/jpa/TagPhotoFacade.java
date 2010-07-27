/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;
import net.wazari.dao.jpa.entity.JPATagPhoto;

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
        String rq = "SELECT DISTINCT tp " +
                "FROM JPAPhoto photo, JPATagPhoto tp " +
                "WHERE photo.album = :album " +
                "AND photo.id = tp.photo ";
        return em.createQuery(rq)
                .setParameter("album", enrAlbum)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }

    @Override
    public TagPhoto loadByTagPhoto(int tagId, int photoId) {
        String rq = "FROM JPATagPhoto tp WHERE tp.photo.id = :photoId AND tp.tag.id = :tagId";
        return (JPATagPhoto) em.createQuery(rq)
                .setParameter("photoId", photoId)
                .setParameter("tagId", tagId)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getSingleResult();
    }

    @Override
    public List<Tag> selectDistinctTags() {
        String rq = "SELECT DISTINCT tp.tag FROM JPATagPhoto tp";
        return em.createQuery(rq).getResultList();
    }

    @Override
    public List<Tag> selectUnusedTags(ServiceSession session) {
        String rq = "SELECT DISTINCT tp.tag " +
                            "FROM JPATagPhoto tp, JPAPhoto p, JPAAlbum a " +
                            "WHERE " +
                            " tp.photo = p.id AND p.album = a.id " +
                            " AND a.theme = :themeId ";
        return em.createQuery(rq)
                .setParameter("themeId", session.getTheme())
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
        String rq = "SELECT o FROM JPATagPhoto o";
        return (List<TagPhoto>) em.createQuery(rq)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.readOnly", true)
                .getResultList();
    }
}
