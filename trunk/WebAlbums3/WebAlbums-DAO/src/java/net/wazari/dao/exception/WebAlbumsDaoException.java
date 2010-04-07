/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.exception;

import net.wazari.common.exception.WebAlbumsException;

/**
 *
 * @author kevin
 */
public class WebAlbumsDaoException extends WebAlbumsException {

    public WebAlbumsDaoException(ErrorType type, String msg) {
        super(type, msg);
    }

    public WebAlbumsDaoException(ErrorType type, String msg, Throwable th) {
        super(type, msg, th);
    }

    public WebAlbumsDaoException(ErrorType type, Throwable th) {
        super(type, th);
    }
   
}
