/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.JnetException;
import com.jnetfs.core.relay.JnetJNIConnector;

public class JnetWrite {
    
    public static byte[] getData(JnetJNIConnector conn) throws JnetException {
        return conn.getBytes("buffer");
    }

    public static long getSize(JnetJNIConnector conn) throws JnetException {
        return conn.getLong("size");
    }

    public static long getOffset(JnetJNIConnector conn) throws JnetException {
        return conn.getLong("offset");
    }
    
    public static long getHandle(JnetJNIConnector conn) throws JnetException {
        return conn.getLong(".fh");
    }
}
