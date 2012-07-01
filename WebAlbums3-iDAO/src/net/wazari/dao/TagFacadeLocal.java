/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.exchange.ServiceSession;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface TagFacadeLocal {
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void create(Tag tag);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void edit(Tag tag);
    
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void remove(Tag tag);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Map<Tag,Long> queryIDNameCount(ServiceSession session) ;

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    List<Tag> queryAllowedTagByType(ServiceSession session, int type) ;

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Tag loadByName(String nom) ;

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    List<Tag> loadVisibleTags(ServiceSession sSession, boolean restrictToGeo) ;
    
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    List<Tag> getNoSuchTags(ServiceSession sSession, List<Tag> tags) ;
    
    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Tag find(Integer integer);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    List<Tag> findAll();

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Tag newTag();

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Set<Tag> getChildren(Tag tag);
}
