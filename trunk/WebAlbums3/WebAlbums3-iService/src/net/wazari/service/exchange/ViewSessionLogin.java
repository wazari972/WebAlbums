/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import java.security.Principal;
import net.wazari.dao.entity.Theme;

/**
 *
 * @author kevinpouget
 */
public interface ViewSessionLogin extends ViewSession {

        void setTheme(Theme enrTheme);

        void setDetails(Boolean newValue);

        void setSessionManager(Boolean sessionManager);

        void setUserId(Integer userId);

        void setRootSession(Boolean asThemeManager);

        void setUserName(String userName);

        
        String getUserPass();

        boolean isAuthenticated();

        Principal getUserPrincipal();

        void login(String user, String passwd);

        void setEditionMode(EditMode editMode);

        Integer getThemeId();
    }