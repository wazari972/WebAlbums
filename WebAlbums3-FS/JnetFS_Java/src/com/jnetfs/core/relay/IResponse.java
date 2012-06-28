/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay;

import java.io.Serializable;

import com.jnetfs.core.Code;

/**
 * response interface
 *
 * @author jacky
 */
public interface IResponse extends Code, Serializable {

    /**
     * return error code
     *
     * @return int
     */
    public int getErrCode();

    /**
     * return JnetJNIConnector
     *
     * @return JnetJNIConnector
     */
    public JnetJNIConnector getConnector();

    /**
     * check is ok or not
     *
     * @return boolean
     */
    public boolean isOK();
}
