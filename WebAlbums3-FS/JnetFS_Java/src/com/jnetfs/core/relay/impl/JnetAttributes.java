/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.relay.JnetJNIConnector;

public class JnetAttributes {

    public static void setMode(JnetJNIConnector conn, int mode) {
        conn.setInteger("st_mode", mode);
    }

    public static void setTime(JnetJNIConnector conn, long time) {
        conn.setLong("st_mtim", time);
    }

    public static void setLinks(JnetJNIConnector conn, long links) {
        conn.setLong("st_nlink", links);
    }

    public static void setSize(JnetJNIConnector conn, long size) {
        conn.setLong("st_size", size);
    }
}
