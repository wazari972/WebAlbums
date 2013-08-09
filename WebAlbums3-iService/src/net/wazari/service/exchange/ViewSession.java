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
    interface VSession {
        ViewSession getVSession();
    }

    enum Edit_Action {
        EDIT, SUBMIT
    }
    
    enum Box {
        NONE, MULTIPLE, LIST, MAP_SCRIPT, MAP
    }

    enum Tag_Mode {
        TAG_USED, TAG_NUSED, TAG_ALL, TAG_NEVER, TAG_NEVER_EVER, TAG_GEO
    }
    
    interface ViewSessionChoix extends VSession {
        enum Choix_Special {
            MAP, JUST_THEME
        }

        boolean getCompleteChoix();
        Choix_Special getSpecial();
    }
    
    interface SessionConfig extends VSession {
        void setDirectFileAccess(boolean access);
        void setStatic(boolean statik);
    }
    
    Integer getStarLevel();    
    Utilisateur getUser();
    boolean isAuthenticated();
    File getTempDir();
    Configuration getConfiguration();
    Integer getThemeId();
    boolean isRemoteAccess();
    boolean directFileAccess();
    boolean getStatic();
}
