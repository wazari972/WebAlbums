/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.exchange;

/**
 *
 * @author kevin
 */
public interface ServiceSession {

    Integer getThemeId();

    Integer getUserId();

    boolean isRootSession();

    boolean isSessionManager();

    int getAlbumSize() ;
    int getPhotoSize() ;
}
