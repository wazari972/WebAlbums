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

public class JnetOpen {

    public static void setHandle(JnetJNIConnector conn, long handle) {
        conn.setLong(".fh", handle);
    }

    public static void setDirectIO(JnetJNIConnector conn, boolean direct) {
        conn.setLong(".direct_io", direct ? 1 : 0);
    }

    public static void setKeepCache(JnetJNIConnector conn, boolean cache) {
        conn.setLong(".keep_cache", cache ? 1 : 0);
    }

    public static long getFlags(JnetJNIConnector conn) throws JnetException {
        return conn.getLong(".flags");
    }
}
