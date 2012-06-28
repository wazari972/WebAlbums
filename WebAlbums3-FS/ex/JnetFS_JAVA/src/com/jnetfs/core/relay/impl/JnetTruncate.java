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

public class JnetTruncate extends JnetOperate {

    public static JnetTruncate instance = new JnetTruncate();

    public JnetTruncate() {
        super("truncate");
    }

    public static long getOffset(JnetJNIConnector conn) throws JnetException {
        return conn.getLong("offset");
    }
}
