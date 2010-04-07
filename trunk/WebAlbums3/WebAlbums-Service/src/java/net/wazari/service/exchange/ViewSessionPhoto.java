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

    Boolean getSuppr();

    String getUser();

    Integer[] getTags();

    String getDesc();

    Boolean getRepresent();

    Integer getTagPhoto();

    Integer getPage();

    EditMode getEditionMode();

    Turn getTurn();

    Integer getAddTag();

    Boolean getChk(Integer id);

    Integer getRmTag();

    Boolean wantsDetails();

    enum Turn {
        RIGHT, LEFT, TAG, UNTAG, MVTAG, RIEN
    }

    Mode getMode();
    Integer getCount() ;
    Integer getAlbum() ;
    Integer getAlbmCount() ;
}
