/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import javax.ejb.Local;
import net.wazari.dao.DatabaseFacadeLocal.DatabaseFacadeLocalException;

/**
 *
 * @author kevinpouget
 */
@Local
public interface MaintFacadeLocal {
    void treatImportXML(boolean protect, String path) throws DatabaseFacadeLocalException  ;

    void treatExportXML(String path) throws DatabaseFacadeLocalException ;

    void treatTruncateDB(boolean protect) ;
    
    void treatFullImport(boolean protect, String path) throws DatabaseFacadeLocalException ;

    void treatUpdate();
}
