/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay;

/**
 * request interface
 *
 * @author jacky
 */
public interface IRequest extends java.io.Serializable {

    /**
     * return request command
     *
     * @return command
     */
    public String getCommand();

    /**
     * return JnetJNIConnector
     *
     * @return JnetJNIConnector
     */
    public JnetJNIConnector getConnector();
}
