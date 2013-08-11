/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import net.wazari.service.exchange.ViewSession.Edit_Action;
import net.wazari.service.exchange.ViewSession.VSession;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;

/**
 *
 * @author kevin
 */
public interface ViewSessionTag extends VSession {
    enum Special {
        CLOUD, PERSONS, PLACES, ABOUT
    }
    interface ViewSessionTagSimple extends VSession {
        Integer getId();
    }
    interface ViewSessionTagCloud extends VSession {
        boolean getWantUnusedTags();
    }
    interface ViewSessionTagEdit extends VSession {
        Integer[] getTagAsked();
        Integer getPage();
        
        ViewSessionPhotoEdit getSessionPhotoEdit();
    }
    interface ViewSessionTagDisplay extends VSession {
        Integer[] getTagAsked();
        Integer getPage();
        boolean getWantTagChildren();
    }

    Special getTagSpecial();
    Edit_Action getEditAction();
    
    ViewSessionTagCloud getSessionTagCloud();
    ViewSessionTagSimple getSessionTagSimple();
    ViewSessionTagEdit getSessionTagEdit();
    ViewSessionPhotoSubmit getSessionPhotoSubmit();
    ViewSessionTagDisplay getSessionTagDisplay();
}
