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
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface UtilisateurFacadeLocal {
    final static String MANAGER_ROLE = "MANAGER" ;
    final static String VIEWER_ROLE = "VIEWER" ;

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void newUser(int id, String name);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Utilisateur loadByName(String name) ;

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Utilisateur loadUserOutside(int albmId) ;

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    List<Utilisateur> loadUserInside(int id) ;

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Utilisateur find(Integer droit);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    List<Utilisateur> findAll();
}
