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

    String getWidth();

    boolean getSuppr();

    String getUser();

    Integer[] getTags();
    
    Integer[] getNewTag();
    
    String getDesc();

    boolean getRepresent();

    Integer getTagPhoto();

    Integer getPage();

    Turn getTurn();

    Integer getAddTag();

    boolean getChk(Integer id);

    Integer getRmTag();

    boolean wantsDetails();

    enum Turn {
        RIGHT, LEFT, TAG, UNTAG, MVTAG, RIEN
    }

    Mode getMode();
    Integer getCount() ;
    Integer getAlbum() ;
    Integer getAlbmCount() ;
}
