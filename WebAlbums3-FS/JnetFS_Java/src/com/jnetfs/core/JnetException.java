/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core;

import java.io.IOException;

public class JnetException extends IOException {

    private static final long serialVersionUID = -5241023121352194809L;
    private int err;

    public JnetException(int err, String msg) {
        super(msg);
        this.err = err;
    }

    public int getCode() {
        return err;
    }
}
