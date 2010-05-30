/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.util.logging.Logger;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.*;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;

/**
 *
 * @author kevin
 */
@Stateless
public class TagPhotoFacade implements TagPhotoFacadeLocal {
    private static final Logger log = Logger.getLogger(TagPhotoFacade.class.getName()) ;
    @PersistenceContext
    private EntityManager em;

    @EJB PhotoFacadeLocal photoDAO ;
    @EJB AlbumFacadeLocal albumDAO ;
    @EJB TagFacadeLocal   tagDAO ;
    @EJB ThemeFacadeLocal themeDAO ;

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void create(TagPhoto tagPhoto) {
        em.persist(tagPhoto);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void edit(TagPhoto tagPhoto) {
        em.merge(tagPhoto);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void remove(TagPhoto tagPhoto) {
        em.remove(tagPhoto);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public void deleteByPhoto(Photo enrPhoto) {
        for (TagPhoto enrTp : enrPhoto.getTagPhotoList()) {
            remove(enrTp);
        }
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public List<TagPhoto> queryByAlbum(Album enrAlbum) {
        String rq = "SELECT DISTINCT tp " +
                "FROM Photo photo, TagPhoto tp " +
                "WHERE photo.album = :album " +
                "AND photo.id = tp.photo ";
        return em.createQuery(rq)
                .setParameter("album", enrAlbum)
                .getResultList();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    public TagPhoto loadByTagPhoto(int tagId, int photoId) {
        String rq = "FROM TagPhoto tp WHERE tp.photo.id = :photoId AND tp.tag.id = :tagId";
        return (TagPhoto) em.createQuery(rq)
                .setParameter("photoId", photoId)
                .setParameter("tagId", tagId)
                .getSingleResult();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public List<Tag> selectDistinctTags() {
        String rq = "SELECT DISTINCT tp.tag FROM TagPhoto tp";
        return em.createQuery(rq).getResultList();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    public List<Tag> selectUnusedTags(ServiceSession session) {
        String rq = "SELECT DISTINCT tp.tag " +
                            "FROM TagPhoto tp, Photo p, Album a " +
                            "WHERE " +
                            " tp.photo = p.id AND p.album = a.id " +
                            " AND a.theme = :themeId ";
        return em.createQuery(rq)
                .setParameter("themeId", session.getTheme())
                .getResultList();
    }
}
