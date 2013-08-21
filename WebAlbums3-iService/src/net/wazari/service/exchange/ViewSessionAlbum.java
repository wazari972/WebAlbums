/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import net.wazari.service.exchange.ViewSession.Edit_Action;
import net.wazari.service.exchange.ViewSession.VSession;
import net.wazari.service.exchange.ViewSessionLogin.ViewSessionTempTheme;

/**
 *
 * @author kevin
 */
public interface ViewSessionAlbum extends VSession {
    enum Album_Special {
        AGO, YEARS, TOP5, SELECT, GRAPH, ABOUT, GPX, PHOTOALBUM_SIZE
    }
    
    interface ViewSessionAlbumAgo extends VSession {
        Integer getYear();
        Integer getMonth();
        Integer getDay();
        boolean getAll();
    }
    
    interface ViewSessionAlbumSimple extends VSession {
        Integer getId();
    }
    
    interface ViewSessionAlbumSubmit extends ViewSessionAlbumSimple {
        String getDesc();

        String getNom();

        String getDate();

        Integer[] getTags();
        boolean getForce();

        boolean getSuppr() ;
        Integer getUserAllowed();
        
        int getNewTheme();
    }
    
    interface ViewSessionAlbumEdit extends ViewSessionAlbumSimple {
        Integer getPage() ;
    }
    interface ViewSessionAlbumDisplay extends ViewSessionAlbumSimple {
        Integer getPage() ;
        Integer getAlbmPage();
    }
    
    interface ViewSessionAlbumYear extends VSession {
        Integer getNbPerYear();
    }
    
    interface ViewSessionAlbumSelect extends VSession {
        boolean getWantTags();
        Integer[] getTagAsked() ;
    }
    
    interface ViewSessionPhotoAlbumSize extends VSession {
        void setPhotoAlbumSize(int photoAlbumSize);
        Integer getNewPhotoAlbumSize();
    }
    
    Album_Special getAlbumSpecial();
    Edit_Action getEditAction();
    
    ViewSessionAlbumEdit getSessionAlbumEdit();
    ViewSessionAlbumSubmit getSessionAlbumSubmit();
    ViewSessionAlbumSimple getSessionAlbumSimple();
    ViewSessionAlbumSelect getSessionAlbumSelect();
    ViewSessionAlbumAgo getAgoSession();
    ViewSessionAlbumDisplay getSessionAlbumDisplay();
    ViewSessionAlbumYear getSessionAlbumYear();
    ViewSessionPhotoAlbumSize getPhotoAlbumSizeSession();
    ViewSessionTempTheme getTempThemeSession();
}
