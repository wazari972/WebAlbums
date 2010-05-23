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

    File getTempDir();
    Integer getThemeId() ;
    Theme getTheme() ;
    void setTheme(Theme enrTheme);

    void setEditionMode(EditMode editMode);

    boolean getDetails();

    void setDetails(Boolean newValue);

    void setTempDir(File temp);
    
    void setSessionManager(Boolean sessionManager) ;

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

    enum Type {

        PHOTO, ALBUM
    }

    enum Box {

        NONE, MULTIPLE, LIST, MAP, MAP_SCRIPT
    }

    enum Mode {

        TAG_USED, TAG_NUSED, TAG_ALL, TAG_NEVER, TAG_GEO
    };

    EditMode getEditionMode();

    Special getSpecial();

    Action getAction();
    
    String getUserName();

    String getUserPass();

    void setUserId(Integer userId);

    void setRootSession(Boolean asThemeManager);

    void setUserName(String userName);

    Integer getId();

    boolean authenticate() ;
    boolean isAuthenticated() ;
    Principal getUserPrincipal() ;
    void login(String user, String passwd) ;
}
