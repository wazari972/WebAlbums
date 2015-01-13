/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import java.security.Principal;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.exchange.ViewSession.VSession;

/**
 *
 * @author kevinpouget
 */
public interface ViewSessionLogin extends VSession {
    enum Login_Action {
        LOGIN, CHANGE_IS_MANAGER,
    }
    
    Login_Action getLoginAction();
    
    void setTheme(Theme enrTheme);

    void setSessionManager(Boolean sessionManager);

    void setRootSession(Boolean asThemeManager);

    void setUser(Utilisateur enrUser);

    boolean isAuthenticated();

    Principal getUserPrincipal();

    void login(String user, String passwd);
    
    String getUserName();

    String getUserPass();

    Boolean dontRedirect();
    
    Boolean getwantManager();
    
    interface ViewSessionTempTheme extends VSession {
        void setTempTheme(Theme enrTheme);
    }
}
