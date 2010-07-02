/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import java.security.Principal;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevinpouget
 */
public interface ViewSessionLogin extends ViewSession {

        void setTheme(Theme enrTheme);

        void setDetails(Boolean newValue);

        void setSessionManager(Boolean sessionManager);

        void setRootSession(Boolean asThemeManager);

        void setUser(Utilisateur enrUser);

        boolean isAuthenticated();

        Principal getUserPrincipal();

        void login(String user, String passwd);

        void setEditionMode(EditMode editMode);

        String getUserName();

        String getUserPass();
    }