/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exception;

import net.wazari.common.exception.WebAlbumsException;

/**
 *
 * @author kevin
 */
public class WebAlbumsServiceException extends WebAlbumsException {

    public WebAlbumsServiceException(ErrorType type, String msg) {
        super(type, msg);
    }

    public WebAlbumsServiceException(ErrorType type, String msg, Throwable th) {
        super(type, msg, th);
    }

    public WebAlbumsServiceException(ErrorType type, Throwable th) {
        super(type, th);
    }
}
