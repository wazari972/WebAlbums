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

public class JnetTouch extends JnetOperate {

    public static JnetTouch instance = new JnetTouch();

    public JnetTouch() {
        super("touch");
    }

    public static long getSecond(JnetJNIConnector conn) throws JnetException {
        return conn.getLong("timespec_0.tv_sec");
    }

    public static long getMillionSecond(JnetJNIConnector conn) throws JnetException {
        return conn.getLong("timespec_0.tv_nsec");
    }
}
