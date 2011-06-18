/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface ViewSessionCarnet extends ViewSession {
    interface ViewSessionAlbumSubmit extends ViewSessionAlbum {
        String getDesc();

        String getNom();

        String getDate();

        Integer[] getTags();
        boolean getForce();

        boolean getSuppr() ;
        Integer getUserAllowed();
    }

    interface ViewSessionAlbumEdit extends ViewSessionAlbum {

        Integer getCountAlbm();

    }
    interface ViewSessionAlbumDisplay extends ViewSessionAlbum {

        Integer getCountAlbm();

    }
    Integer getId();
    Integer getCount();
    Integer getPage() ;
    
}
