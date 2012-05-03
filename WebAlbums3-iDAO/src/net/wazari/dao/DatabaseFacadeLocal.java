/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE})
public interface DatabaseFacadeLocal {
    class DatabaseFacadeLocalException extends Exception {
        public DatabaseFacadeLocalException() {}
        public DatabaseFacadeLocalException(String msg) {
            super(msg);
        }
        public DatabaseFacadeLocalException(Throwable reason) {
            super(reason);
        }
    }
    
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void treatImportXML(boolean protect, String path)
            throws DatabaseFacadeLocalException;

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void treatExportXML(String path) 
            throws DatabaseFacadeLocalException;
    
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void treatTruncateDB(boolean protect)
            throws DatabaseFacadeLocalException;
}
