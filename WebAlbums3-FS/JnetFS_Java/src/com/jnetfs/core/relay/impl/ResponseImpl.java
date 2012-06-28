/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.relay.IResponse;
import com.jnetfs.core.relay.JnetJNIConnector;

/**
 * default implement
 *
 * @author jacky
 */
public class ResponseImpl implements IResponse {
    //SSID

    private static final long serialVersionUID = 8115461040119845347L;
    //static one
    private static ResponseImpl impl = new ResponseImpl();
    // error code
    private int error;
    //JnetJNIConnector object
    private JnetJNIConnector connector;

    /**
     * return error code
     *
     * @return int
     */
    public int getErrCode() {
        return error;
    }

    /**
     * return parcel object
     *
     * @return IParcel
     */
    /**
     * return JnetJNIConnector
     *
     * @return JnetJNIConnector
     */
    public JnetJNIConnector getConnector() {
        return connector;
    }

    /**
     * check is ok or not
     *
     * @return boolean
     */
    public boolean isOK() {
        return error == ESUCCESS;
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that "textually represents"
     * this object. The result should be a concise but informative
     * representation that is easy for a person to read. It is recommended that
     * all subclasses override this method. <p> The
     * <code>toString</code> method for class
     * <code>Object</code> returns a string consisting of the name of the class
     * of which the object is an instance, the at-sign character `
     * <code>@</code>', and the unsigned hexadecimal representation of the hash
     * code of the object. In other words, this method returns a string equal to
     * the value of: <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuffer buff = new StringBuffer();
        if (error != IResponse.ESUCCESS) {
            buff.append("Code:").append(error);
        }
        if (connector != null) {
            buff.append(connector);
        }
        return buff.toString();
    }

    /**
     * get getInstance
     *
     * @param code int
     * @param connector JnetJNIConnector
     * @return IRequest
     */
    public static IResponse getInstance(int code) {
        return getInstance(code, null);
    }

    /**
     * get getInstance
     *
     * @param code int
     * @param connector JnetJNIConnector
     * @return IRequest
     */
    public static IResponse getInstance(int code, JnetJNIConnector connector) {
        impl.error = code;
        impl.connector = connector;
        return impl;
    }
}
