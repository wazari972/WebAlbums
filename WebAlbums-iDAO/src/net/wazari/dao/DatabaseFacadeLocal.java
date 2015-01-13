/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao;

import javax.ejb.Local;

/**
 *
 * @author kevin
 */
@Local
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
    
    void treatImportXML(boolean protect, String path)
            throws DatabaseFacadeLocalException;

    void treatExportXML(String path) 
            throws DatabaseFacadeLocalException;
    
    void treatTruncateDB(boolean protect) throws DatabaseFacadeLocalException;
}
