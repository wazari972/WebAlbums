/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.DatabaseFacadeLocal.DatabaseFacadeLocalException;

/**
 *
 * @author kevinpouget
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface MaintFacadeLocal {
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void treatImportXML(boolean protect, String path) throws DatabaseFacadeLocalException  ;

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void treatExportXML(String path) throws DatabaseFacadeLocalException ;

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void treatTruncateDB(boolean protect) ;
    
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void treatFullImport(boolean protect, String path) throws DatabaseFacadeLocalException ;

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void treatUpdate();
}
