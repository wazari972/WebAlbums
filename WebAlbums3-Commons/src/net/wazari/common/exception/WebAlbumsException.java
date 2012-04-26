/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.exception;

/**
 *
 * @author kevinpouget
 */
public class WebAlbumsException extends Exception {
    public enum ErrorType {
        JDBCException,
        AuthenticationException,
        DatabaseException
    }
    public static final ErrorType JDBCException = ErrorType.JDBCException;

    private ErrorType type ;
    public WebAlbumsException(ErrorType type) {
        this.type = type;
    }
    public WebAlbumsException(ErrorType type, Throwable th) {
        super(th);
        this.type = type;
    }

    public WebAlbumsException(ErrorType type, String msg, Throwable th) {
        super(msg, th);
        this.type = type;
    }

    public WebAlbumsException(ErrorType type, String msg) {
        super(msg);
        this.type = type;
    }

    public ErrorType getType() {
        return type;
    }
}
