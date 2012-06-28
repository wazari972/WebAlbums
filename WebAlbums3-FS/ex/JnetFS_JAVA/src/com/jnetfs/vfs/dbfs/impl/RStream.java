/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.vfs.dbfs.impl;

import java.io.IOException;

import com.jnetfs.core.vfs.MStream;
import com.jnetfs.core.vfs.VFile;

public class RStream extends MStream {

    private static final long serialVersionUID = -4527922593102206242L;

    /**
     * constructor
     *
     * @param parent VFile
     * @param name name
     * @throws IOException IOException
     */
    public RStream(VFile parent, String name) throws IOException {
        super(parent, name);
    }

    /**
     * check the update time
     */
    public void touch() {
        super.touch();
        setLoaded(false);
    }
}
