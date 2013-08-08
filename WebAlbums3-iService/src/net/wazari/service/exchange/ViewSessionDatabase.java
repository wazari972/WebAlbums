/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange;

import net.wazari.dao.entity.Theme;
import net.wazari.service.exchange.ViewSession.Database_Action;

/**
 *
 * @author kevin
 */
public interface ViewSessionDatabase {
    void setRootSession(Boolean rootSession) ;
    void setTheme(Theme enrTheme);
    
    Database_Action getAction();
    ViewSession getVSession();
}
