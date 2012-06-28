/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

public class JnetDelete extends JnetOperate {

    public static JnetDelete instance = new JnetDelete();

    public JnetDelete() {
        super("delete");
    }
}
