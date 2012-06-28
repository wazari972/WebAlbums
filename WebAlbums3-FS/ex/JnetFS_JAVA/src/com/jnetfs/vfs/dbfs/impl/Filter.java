/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.vfs.dbfs.impl;

import java.io.IOException;

import com.jnetfs.core.vfs.VFile;

public class Filter extends RStream {

    private static final long serialVersionUID = 2510112302936969139L;
    public static final String NAME = "filter";

    /**
     * constructor
     *
     * @param parent VFile
     * @throws IOException IOException
     */
    protected Filter(VFile parent) throws IOException {
        super(parent, NAME);
        setMode(0664);
    }

    /**
     * check the update time
     */
    public void touch() {
        super.touch();
        try {
            VFile.find(getParent(), Data.NAME).touch();
        } catch (IOException ex) {
        }
    }
}
