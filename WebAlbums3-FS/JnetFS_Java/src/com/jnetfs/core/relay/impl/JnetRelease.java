/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

public class JnetRelease extends JnetOperate {

    public static JnetRelease instance = new JnetRelease();

    public JnetRelease() {
        super("release");
    }
}
