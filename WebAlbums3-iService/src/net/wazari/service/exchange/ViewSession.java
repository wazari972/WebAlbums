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
public interface ViewSession extends ServiceSession{
    public interface ViewSessionChoix {
        Choix_Special getSpecial();
        ViewSession getVSession();
    }
    enum Old_Special {
        UPDATE, RANDOM, ONLY, 
    }
    
    enum Choix_Special {
        MAP, JUST_THEME
    }
    
    enum Carnet_Special {
        TOP5
    }
    
    enum Tag_Special {
        CLOUD, PERSONS, PLACES
    }
    
    enum Photo_Special {
        VISIONNEUSE, FASTEDIT
    }
    
    enum Album_Special {
        AGO, YEARS, TOP5, SELECT, GRAPH, ABOUT, GPX, PHOTOALBUM_SIZE
    }

    enum Action_Photo {
        EDIT, SUBMIT, MASSEDIT
    }
    
    enum Album_Action {
        EDIT, SUBMIT
    }
    
    enum Carnet_Action {
        EDIT, SUBMIT, SAVE
    }
    
    enum Old_Action {
        LOGIN, CHANGE_IS_MANAGER, IMPORT, DEFAULT
    }
    
    enum Box {
        NONE, MULTIPLE, LIST, MAP_SCRIPT, MAP
        
    }
    enum Config_Action {
        DELTAG, MODGEO, MODVIS, MODTAG, NEWTAG, DELTHEME, LINKTAG, MODPERS, 
        MODMINOR
    }

    enum Database_Action {
        TRUNK, EXPORT, CHECK_DB, CHECK_FS, STATS, UPDATE, UPDATE_DAO, 
        PLUGINS, RELOAD_PLUGINS, CREATE_DIRS, SAVE_CONFIG, RELOAD_CONFIG, 
        PRINT_CONFIG, SETHOME
    }

    enum Mode {
        TAG_USED, TAG_NUSED, TAG_ALL, TAG_NEVER, TAG_NEVER_EVER, TAG_GEO
    }

    Integer getStarLevel();
    
    boolean getCompleteChoix();

    Old_Special getSpecial();

    Old_Action getAction();

    Utilisateur getUser();

    boolean isAuthenticated();

    File getTempDir();

    Configuration getConfiguration();
    
    Integer getThemeId();

    boolean isRemoteAccess();
    
    boolean directFileAccess();
    
    void setDirectFileAccess(boolean access);
    
    void setStatic(boolean statik);
    
    boolean getStatic();
}
