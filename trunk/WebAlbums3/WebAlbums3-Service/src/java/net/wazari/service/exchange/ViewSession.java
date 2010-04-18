/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange;

import java.io.File;
import net.wazari.dao.exchange.ServiceSession;

/**
 *
 * @author kevin
 */
public interface ViewSession extends ServiceSession {

    File getTempDir();

    void setThemeName(String nom);

    void setThemeId(Integer newID);

    void setEditionMode(EditMode editMode);

    Boolean getDetails();

    void setDetails(Boolean newValue);

    void setTempDir(File temp);

    Configuration getConfiguration();

    enum Special {

        TOP5, FULLSCREEN, CLOUD, PERSONS, PLACES, RSS
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

    String getThemeName();

    String getUserName();

    String getUserPass();

    Integer getUserId() ;

    void setUserId(Integer userId);

    void setRootSession(boolean asThemeManager);

    void setUserName(String userName);

    Integer getId();
}
