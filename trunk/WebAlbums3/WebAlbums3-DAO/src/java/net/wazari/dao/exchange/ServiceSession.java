/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.exchange;

import net.wazari.dao.entity.Theme;

/**
 *
 * @author kevin
 */
public interface ServiceSession {

    Theme getTheme();

    Integer getUserId();

    boolean isRootSession();

    boolean isSessionManager();

    int getAlbumSize() ;
    int getPhotoSize() ;
}
