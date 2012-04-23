/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange;

import java.io.File;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.exchange.ServiceSession;

/**
 *
 * @author kevin
 */
public interface ViewSession extends ServiceSession {
    enum Special {
        TOP5, FULLSCREEN, CLOUD, PERSONS, PLACES, RSS, UPDATE, YEARS
    }

    enum Action {

        SUBMIT, EDIT, IMPORT, DELTAG, MODGEO, MODVIS, MODTAG, NEWTAG, MASSEDIT, LOGIN, DELTHEME
    }

    enum EditMode {

        VISITE, NORMAL, EDITION
    }

    enum Box {

        NONE, MULTIPLE, LIST, MAP, MAP_SCRIPT
    }

    enum Mode {

        TAG_USED, TAG_NUSED, TAG_ALL, TAG_NEVER, TAG_GEO
    }

    EditMode getEditionMode();

    Special getSpecial();

    Action getAction();

    Utilisateur getUser();

    boolean isAuthenticated();

    File getTempDir();

    boolean getDetails();

    Configuration getConfiguration();
    
    Integer getThemeId();

    boolean isRemoteAccess();
}
