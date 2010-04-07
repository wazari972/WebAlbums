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

    @EJB PhotoFacade photoDAO ;

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
        return em.createQuery("select object(o) from TagPhoto as o").getResultList();
    }

    public void deleteByPhoto(int photoID) {
        Photo enrPhoto = photoDAO.find(photoID) ;

        for (TagPhoto enrTp : enrPhoto.getTagPhotoList()) {
            remove(enrTp);
        }
    }

    public List<TagPhoto> queryByPhoto(int photoID) {
        String rq = "FROM TagPhoto WHERE photo = '" + photoID + "'";
        return em.createQuery(rq).getResultList();
    }

    public List<TagPhoto> queryByAlbum(int albumID) {
        String rq = "SELECT DISTINCT tp " +
                "FROM Photo photo, TagPhoto tp " +
                "WHERE photo.Album = '" + albumID + "' " +
                "AND photo.ID = tp.Photo ";
        return em.createQuery(rq).getResultList();
    }

    public TagPhoto loadByTagPhoto(int tagID, int photoID) {
        String rq = "FROM TagPhoto WHERE photo = '" + photoID + "' AND tag = '" + tagID + "'";
        return (TagPhoto) em.createQuery(rq).getSingleResult();
    }

    public List<TagPhoto> queryByTag(int tagID) {
        String rq = "FROM TagPhoto WHERE tag = '" + tagID + "'";
        return em.createQuery(rq).getResultList();
    }

    public List<Tag> selectDistinctTags() {
        String rq = "SELECT DISTINCT tp.Tag FROM TagPhoto tp";
        return em.createQuery(rq).getResultList();
    }


    public List<Tag> selectUnusedTags(ServiceSession sSession) {
        String rq = "SELECT DISTINCT tp.Tag " +
                            "FROM TagPhoto tp, Photo p, Album a " +
                            "WHERE " +
                            " tp.Photo = p.ID AND p.Album = a.ID " +
                            " AND a.Theme = '" + sSession.getThemeId() + "' ";
        return em.createQuery(rq).getResultList();
    }
}
