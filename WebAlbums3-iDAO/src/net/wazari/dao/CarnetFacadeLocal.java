/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Carnet;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface CarnetFacadeLocal {
    
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    List<Carnet> findAll();

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void create(Carnet carnet);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void edit(Carnet carnet);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void remove(Carnet carnet);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Carnet find(Integer carnet);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Carnet newCarnet();
    
    
}
