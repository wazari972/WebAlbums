/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

public class JnetRmdir extends JnetOperate {

    public static JnetRmdir instance = new JnetRmdir();

    public JnetRmdir() {
        super("rmdir");
    }
}
