/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.relay.JnetJNIConnector;

public class JnetReadLink extends JnetOperate {

    public static JnetReadLink instance = new JnetReadLink();

    public JnetReadLink() {
        super("readlink");
    }

    public static void setRealPath(JnetJNIConnector conn, String name) {
        conn.setString("realPath", name);
    }
}
