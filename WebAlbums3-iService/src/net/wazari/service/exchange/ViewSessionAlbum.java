/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface ViewSessionAlbum extends ViewSession {
    interface ViewSessionAlbumSubmit extends ViewSessionAlbum {
        String getDesc();

        String getNom();

        String getDate();

        Integer[] getTags();
        boolean getForce();

        boolean getSuppr() ;
        Integer getUserAllowed();
        
        int getNewTheme();
    }

    interface ViewSessionAlbumEdit extends ViewSessionAlbum {}
    interface ViewSessionAlbumDisplay extends ViewSessionAlbum {}
    Integer getId();
    Integer getPage() ;
    Integer getAlbmPage();
    Integer getNbPerYear();
    void setPhotoAlbumSize(int size);
    
    Integer[] getTagAsked() ;
    
    boolean getWantTags();
}
