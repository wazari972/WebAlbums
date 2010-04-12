/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevin
 */
public class ServiceSessionImpl implements ServiceSession {
    private Theme enrTheme ;
    private Utilisateur enrUser ;
    private boolean rootSession ;
    private boolean sessionManager ;

    public boolean isSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(boolean sessionManager) {
        this.sessionManager = sessionManager;
    }
    
    public boolean isRootSession() {
        return rootSession;
    }

    public void setRootSession(boolean rootSession) {
        this.rootSession = rootSession;
    }

    public Integer getThemeId() {
        return enrTheme.getId();
    }

    public void setThemeId(Theme enrTheme) {
        this.enrTheme = enrTheme;
    }

    public Integer getUserId() {
        return enrUser.getId();
    }

    public void setUserId(Utilisateur enrUser) {
        this.enrUser = enrUser;
    }
}
