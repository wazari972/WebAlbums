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
public interface UtilisateurFacadeLocal {
    final static String MANAGER_ROLE = "MANAGER" ;
    final static String VIEWER_ROLE = "VIEWER" ;

    void newUser(int id, String name);

    Utilisateur loadByName(String name) ;

    Utilisateur loadUserOutside(int albmId) ;

    List<Utilisateur> loadUserInside(int id) ;

    Utilisateur find(Integer droit);

    List<Utilisateur> findAll();
}
