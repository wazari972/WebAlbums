/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface ViewSessionPhoto extends ViewSession {

    interface ViewSessionPhotoFastEdit extends ViewSessionPhoto {
        enum TagAction {SET, ADD, RM}
        
        String getDesc();
        
        Integer[] getTagSet();
        
        TagAction getTagAction();
        
        Integer getStars();
        
        Integer getNewStarLevel();
        void setStarLevel(Integer starLevel);
        Integer getId();
    }
    interface ViewSessionPhotoSimple {
        Integer getId();
        ViewSession getVSession();
    }
    interface ViewSessionPhotoSubmit {

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
        
        ViewSession getVSession();
    }
    interface ViewSessionPhotoEdit {
        Integer getId();
        ViewSession getVSession();
    }
    
    interface ViewSessionAnAlbum {
        Integer getAlbum();
        ViewSession getVSession();
    }
    interface ViewSessionPhotoDisplay {
        interface ViewSessionPhotoDisplayMassEdit {

            enum Turn {

                RIGHT, LEFT, TAG, UNTAG, MVTAG, NOTHING, AUTHOR
            }

            Turn getTurn();

            boolean getChk(Integer id);

            Integer[] getAddTags();

            Integer getRmTag();
        }
        Action_Photo getAction();
        ViewSessionPhotoDisplayMassEdit getMassEdit();
        Integer getPage();
        Integer getId();
        Integer getAlbum();
        Integer getAlbmPage();
    
        ViewSession getVSession();
    }
    Mode getMode();    
}
