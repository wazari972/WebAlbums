/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange;

import java.io.File;
import java.security.Principal;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.exchange.ServiceSession;

/**
 *
 * @author kevin
 */
public interface ViewSession extends ServiceSession {

    public String getUserPass();

    interface ViewSessionLogin extends ViewSession {

        void setTheme(Theme enrTheme);

        void setDetails(Boolean newValue);

        

        void setSessionManager(Boolean sessionManager);

        void setUserId(Integer userId);

        void setRootSession(Boolean asThemeManager);

        void setUserName(String userName);

        String getUserPass();

        boolean authenticate();

        Principal getUserPrincipal();

        void login(String user, String passwd);

        void setEditionMode(EditMode editMode);

        Integer getThemeId();
    }

    interface ViewSessionSession extends ViewSession {
        void setTempDir(File temp);
    }


    EditMode getEditionMode();

    Special getSpecial();

    Action getAction();

    String getUserName();

    boolean isAuthenticated();

    File getTempDir();

    @Override
    Theme getTheme();

    boolean getDetails();

    Configuration getConfiguration();

    enum Special {

        TOP5, FULLSCREEN, CLOUD, PERSONS, PLACES, RSS, UPDATE
    }

    enum Action {

        SUBMIT, EDIT, IMPORT, DELTAG, MODGEO, MODVIS, MODTAG, NEWTAG, MASSEDIT, LOGIN
    }

    enum EditMode {

        VISITE, NORMAL, EDITION
    }

    enum Box {

        NONE, MULTIPLE, LIST, MAP, MAP_SCRIPT
    }

    enum Mode {

        TAG_USED, TAG_NUSED, TAG_ALL, TAG_NEVER, TAG_GEO
    };
}
