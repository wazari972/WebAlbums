/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange;

import net.wazari.dao.entity.Theme;
import net.wazari.service.exchange.ViewSession.VSession;

/**
 *
 * @author kevin
 */
public interface ViewSessionDatabase extends VSession {
    enum Database_Action {
        TRUNK, EXPORT, CHECK_DB, CHECK_FS, STATS, UPDATE, UPDATE_DAO, 
        PLUGINS, RELOAD_PLUGINS, CREATE_DIRS, SAVE_CONFIG, RELOAD_CONFIG, 
        PRINT_CONFIG, DEFAULT, IMPORT
    }
    
    Database_Action getDatabaseAction();
    
    void setRootSession(Boolean rootSession) ;
    void setTheme(Theme enrTheme);
}
