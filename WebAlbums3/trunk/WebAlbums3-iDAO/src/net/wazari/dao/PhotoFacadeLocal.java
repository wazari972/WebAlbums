/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao;

import net.wazari.dao.exchange.ServiceSession;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.ADMIN_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface PhotoFacadeLocal {
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void create(Photo photo);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void edit(Photo photo);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void remove(Photo photo);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Photo loadIfAllowed(ServiceSession session, int id);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    SubsetOf<Photo> loadFromAlbum(ServiceSession session, int albumId, Bornes bornes);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Photo loadByPath(String path);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    SubsetOf<Photo> loadByTags(ServiceSession session, List<Integer> listTagId, Bornes bornes);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Photo find(Integer photoID);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Photo newPhoto();
}
