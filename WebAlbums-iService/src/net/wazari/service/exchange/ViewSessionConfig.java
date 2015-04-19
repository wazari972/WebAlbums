/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import net.wazari.service.exchange.ViewSession.VSession;

/**
 *
 * @author kevin
 */
public interface ViewSessionConfig extends VSession {
    enum Config_Special {
        ONLY
    }
    
    enum Config_Action {
        DELTAG, MODGEO, MODVIS, MODTAG, NEWTAG, DELTHEME, LINKTAG, MODPERS, 
        MODMINOR, DEFAULT, IMPORT, SETHOME,
        SCREEN_SIZE
    }
    
    Config_Special getConfigSpecial();
    Config_Action getConfigAction();
    
    boolean getMinor();
    
    String getNouveau();

    Integer getTag();

    String getLng();

    String getLat();

    boolean getVisible();

    String getImportTheme();

    String getNom();

    Integer getType();

    Integer getParentTag();

    Integer[] getSonTags();

    String getBirthdate();
    
    String getContact();
    
    int getScreenSize();
    void setScreenSize(int size);
}
