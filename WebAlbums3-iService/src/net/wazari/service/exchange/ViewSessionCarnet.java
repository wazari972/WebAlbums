/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import java.util.Set;
import net.wazari.service.exchange.ViewSession.VSession;

/**
 *
 * @author kevin
 */
public interface ViewSessionCarnet extends VSession {
    enum Carnet_Action {
        EDIT, SUBMIT, SAVE
    }
    
    interface ViewSessionCarnetSubmit extends VSession {
        Integer getCarnet();
        
        String getDesc();
        String getNom();
        String getDate();

        Integer[] getTags();
        boolean getForce();

        boolean getSuppr() ;
        Integer getUserAllowed();

        String getCarnetText();
        Set<Integer> getCarnetPhoto();
        Integer getCarnetRepr();
        Set<Integer> getCarnetAlbum();
        
    }

    interface ViewSessionCarnetSimple extends VSession {
        Integer getId();
    }
    interface ViewSessionCarnetEdit extends VSession {
        Integer getCarnetsPage();
        Integer getCarnet();
        Integer getPage() ;
    }
    interface ViewSessionCarnetDisplay extends VSession {
        Integer getCarnet();
        Integer getCarnetsPage();
        Integer getPage() ;
    }
    
    enum Carnet_Special {
        TOP5
    }
    
    Carnet_Special getCarnetSpecial();
    Carnet_Action getCarnetAction();
    
    ViewSessionCarnetDisplay getSessionCarnetDisplay();
    ViewSessionCarnetEdit getSessionCarnetEdit();
    ViewSessionCarnetSubmit getSessionCarnetSubmit();
    ViewSessionCarnetSimple getSessionCarnetSimple();
}
