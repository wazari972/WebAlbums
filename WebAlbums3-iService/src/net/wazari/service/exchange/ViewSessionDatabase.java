/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange;

import net.wazari.dao.entity.Theme;

/**
 *
 * @author kevin
 */
public interface ViewSessionDatabase extends ViewSession {
    void setRootSession(Boolean rootSession) ;
    void setTheme(Theme enrTheme);
}
