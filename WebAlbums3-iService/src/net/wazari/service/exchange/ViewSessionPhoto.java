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
public interface ViewSessionPhoto extends VSession {
    enum Photo_Action {
        MASSEDIT, EDIT, SUBMIT
    }
    
    enum Photo_Special {
        VISIONNEUSE, FASTEDIT, ABOUT, RANDOM
    }
    
    interface ViewSessionPhotoFastEdit{
        enum TagAction {SET, ADD, RM}
        
        String getDesc();
        
        Integer[] getTagSet();
        
        TagAction getTagAction();
        
        Integer getStars();
        
        Integer getNewStarLevel();
        
        Integer getId();
        
        void setStarLevel(Integer starLevel);
    }
    
    interface ViewSessionPhotoSimple extends VSession {
        Integer getId();
        
    }
    interface ViewSessionPhotoSubmit extends VSession {

        boolean getSuppr();

        String getDesc();

        boolean getRepresent();

        String getDroit() ;

        Integer[] getNewTag();

        Integer[] getTags();

        boolean getThemeBackground();

        boolean getThemePicture();
        Integer getId();
        Integer getTagPhoto();
    }
    interface ViewSessionPhotoEdit extends VSession {
        Integer getId();
        Integer getAlbum();
        Integer getAlbmPage();
        Integer getPage();
    }
    
    interface ViewSessionAnAlbum extends VSession {
        Integer getAlbum();
    }
    
    interface ViewSessionPhotoDisplayMassEdit {
            enum Turn {
                RIGHT, LEFT, TAG, UNTAG, MVTAG, NOTHING, AUTHOR
            }

            Turn getTurn();
            boolean getChk(Integer id);

            Integer[] getAddTags();
            Integer getRmTag();
    }
    interface ViewSessionPhotoDisplay extends VSession {
        boolean getWantMassedit();
        ViewSessionPhotoDisplayMassEdit getMassEdit();
        Integer getPage();
        Integer getAlbum();
        Integer getAlbmPage();
    }
    Photo_Action getPhotoAction();
    Photo_Special getPhotoSpecial();
    
    ViewSessionPhotoEdit getSessionPhotoEdit();
    ViewSessionPhotoDisplay getSessionPhotoDisplay();
    ViewSessionPhotoSubmit getSessionPhotoSubmit();
    ViewSessionPhotoFastEdit getSessionPhotoFastEdit();
    ViewSessionPhotoSimple getSessionPhotoSimple();
}
