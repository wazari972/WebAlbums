/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.relay.IRequest;
import com.jnetfs.core.relay.JnetJNIConnector;

/**
 * default implement
 *
 * @author jacky
 */
public class RequestImpl implements IRequest {
    //SSID

    private static final long serialVersionUID = 7976678373547473024L;
    //static one
    private static RequestImpl impl = new RequestImpl();
    //JnetJNIConnector  
    private JnetJNIConnector connector = null;
    //command
    private String command;

    /**
     * return request command
     *
     * @return command
     */
    public String getCommand() {
        return command;
    }

    /**
     * return JnetJNIConnector
     *
     * @return JnetJNIConnector
     */
    public JnetJNIConnector getConnector() {
        return connector;
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
        buff.append("Cmd:").append(command);
        if (connector != null) {
            buff.append(connector);
        }
        return buff.toString();
    }

    /**
     * get getInstance
     *
     * @param connector JnetJNIConnector
     * @return IRequest
     */
    public static IRequest getInstance(JnetJNIConnector connector) {
        return getInstance(null, connector);
    }

    /**
     * getInstance
     *
     * @param cmd String
     * @param opts Map<String,Object>
     * @param connector JnetJNIConnector
     * @return IRequest
     */
    public static IRequest getInstance(
            String cmd, JnetJNIConnector connector) {
        impl.command = cmd;
        impl.connector = connector;
        return impl;
    }
}
