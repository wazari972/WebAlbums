/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.ADMIN_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface UtilisateurFacadeLocal {
    final static String ADMIN_ROLE = "ADMIN" ;
    final static String VIEWER_ROLE = "VIEWER" ;

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void newUser(int id, String name);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Utilisateur loadByName(String name) ;

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Utilisateur loadUserOutside(int albmId) ;

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    List<Utilisateur> loadUserInside(int id) ;

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Utilisateur find(Integer droit);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    List<Utilisateur> findAll();
}
