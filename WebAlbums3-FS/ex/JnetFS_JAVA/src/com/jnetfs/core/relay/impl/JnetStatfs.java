/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.relay.JnetJNIConnector;

public class JnetStatfs extends JnetOperate {

    public static JnetStatfs instance = new JnetStatfs();

    public JnetStatfs() {
        super("statfs");
    }

    public static void setNameMaxLen(JnetJNIConnector conn, long val) {
        conn.setLong("f_namemax", val);
    }

    public static void setBlockSize(JnetJNIConnector conn, long val) {
        conn.setLong("f_bsize", val);
    }

    public static void setBlocks(JnetJNIConnector conn, long val) {
        conn.setLong("f_blocks", val);
    }

    public static void setFreeBlocks(JnetJNIConnector conn, long val) {
        conn.setLong("f_bfree", val);
    }

    public static void setAvailableBlocks(JnetJNIConnector conn, long val) {
        conn.setLong("f_bavail", val);
    }

    public static void setFreeSize(JnetJNIConnector conn, long val) {
        conn.setLong("f_frsize", val);
    }

    public static void setFiles(JnetJNIConnector conn, long val) {
        conn.setLong("f_files", val);
    }

    public static void setFileFree(JnetJNIConnector conn, long val) {
        conn.setLong("f_ffree", val);
    }

    public static void setFileAvailable(JnetJNIConnector conn, long val) {
        conn.setLong("f_favail", val);
    }

    public static void setFileSID(JnetJNIConnector conn, long val) {
        conn.setLong("f_fsid", val);
    }
}