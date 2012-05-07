/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao;

import java.util.Collection;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.exchange.ServiceSession.ListOrder;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface PhotoFacadeLocal {
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void create(Photo photo);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void edit(Photo photo);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void remove(Photo photo);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Photo loadIfAllowed(ServiceSession session, int id);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    SubsetOf<Photo> loadFromAlbum(ServiceSession session, Album album, Bornes bornes, ListOrder order);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Photo loadByPath(String path);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    SubsetOf<Photo> loadByTags(ServiceSession session, Collection<Tag> listTagId, Bornes bornes, ListOrder order);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Photo find(Integer photoID);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Photo newPhoto();

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    List<Photo> findAll();

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Photo loadRandom(ServiceSession vSession);
}
