/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;

/**
 *
 * @author kevin
 */
@Stateless
public class TagPhotoFacade implements TagPhotoFacadeLocal {
    @PersistenceContext
    private EntityManager em;

    @EJB PhotoFacadeLocal photoDAO ;
    @EJB AlbumFacadeLocal albumDAO ;
    @EJB TagFacadeLocal   tagDAO ;
    @EJB ThemeFacadeLocal themeDAO ;

    public void create(TagPhoto tagPhoto) {
        em.persist(tagPhoto);
    }

    public void edit(TagPhoto tagPhoto) {
        em.merge(tagPhoto);
    }

    public void remove(TagPhoto tagPhoto) {
        em.remove(em.merge(tagPhoto));
    }

    public TagPhoto find(Object id) {
        return em.find(TagPhoto.class, id);
    }

    public List<TagPhoto> findAll() {
        return em.createQuery("select object(o) from TagPhoto as o")
                .getResultList();
    }

    public void deleteByPhoto(int photoId) {
        Photo enrPhoto = photoDAO.find(photoId) ;

        for (TagPhoto enrTp : enrPhoto.getTagPhotoList()) {
            remove(enrTp);
        }
    }

    public List<TagPhoto> queryByPhoto(int photoId) {
        String rq = "FROM TagPhoto WHERE photo = :photoId";
        return em.createQuery(rq)
                .setParameter("photoId", photoDAO.find(photoId))
                .getResultList();
    }

    public List<TagPhoto> queryByAlbum(int albumId) {
        String rq = "SELECT DISTINCT tp " +
                "FROM Photo photo, TagPhoto tp " +
                "WHERE photo.album = :albumId " +
                "AND photo.id = tp.photo ";
        return em.createQuery(rq)
                .setParameter("albumId", albumDAO.find(albumId))
                .getResultList();
    }

    public TagPhoto loadByTagPhoto(int tagId, int photoId) {
        String rq = "FROM TagPhoto tp WHERE tp.photo = :photoId AND tp.tag = :tagId";
        return (TagPhoto) em.createQuery(rq)
                .setParameter("photoId", photoDAO.find(photoId))
                .setParameter("tagId", tagDAO.find(tagId))
                .getSingleResult();
    }

    public List<TagPhoto> queryByTag(int tagId) {
        String rq = "FROM TagPhoto WHERE tag = :tagId";
        return em.createQuery(rq)
                .setParameter("tagId", tagDAO.find(tagId))
                .getResultList();
    }

    public List<Tag> selectDistinctTags() {
        String rq = "SELECT DISTINCT tp.tag FROM TagPhoto tp";
        return em.createQuery(rq).getResultList();
    }


    public List<Tag> selectUnusedTags(ServiceSession session) {
        String rq = "SELECT DISTINCT tp.tag " +
                            "FROM TagPhoto tp, Photo p, Album a " +
                            "WHERE " +
                            " tp.photo = p.id AND p.album = a.id " +
                            " AND a.theme = :themeId ";
        return em.createQuery(rq)
                .setParameter("themeId", themeDAO.find(session.getThemeId()))
                .getResultList();
    }
}
