/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import net.wazari.service.exchange.ViewSession.Album_Action;
import net.wazari.service.exchange.ViewSession.Album_Special;

/**
 *
 * @author kevin
 */
public interface ViewSessionAlbum {
    interface ViewSessionAlbumAgo {
        Integer getYear();
        Integer getMonth();
        Integer getDay();
        boolean getAll();
        ViewSession getVSession();
    }
    
    interface ViewSessionAlbumSimple {
        Integer getId();
        ViewSession getVSession();
    }
    
    interface ViewSessionAlbumSubmit {
        String getDesc();

        String getNom();

        String getDate();

        Integer[] getTags();
        boolean getForce();

        boolean getSuppr() ;
        Integer getUserAllowed();
        
        int getNewTheme();
        
        Integer getId();
        ViewSession getVSession();
    }

    ViewSession getVSession();
    
    interface ViewSessionAlbumEdit {
        ViewSession getVSession();
        Integer getId();
    }
    interface ViewSessionAlbumDisplay {
        ViewSession getVSession();
        Integer getId();
        Integer getPage() ;
    }
    
    Album_Special getSpecial();
    Album_Action getAction();
    Integer getPage() ;
    
    Integer getAlbmPage();
    Integer getNbPerYear();
    void setPhotoAlbumSize(int size);
    
    Integer[] getTagAsked() ;
    
    boolean getWantTags();
}
