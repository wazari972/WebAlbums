/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.vfs.dbfs.impl;

import java.io.IOException;
import java.sql.Connection;

import com.jnetfs.core.vfs.VFile;

public class Table extends VFile {

    private static final long serialVersionUID = 7121337350781966130L;
    //database
    private transient DB database = null;

    /**
     * constructor
     *
     * @param parent vfile
     * @param name name
     */
    protected Table(DB database, VFile parent, String name) throws IOException {
        super(parent, name);
        this.database = database;
        new Structure(this);
        new Data(this);
        setMode(0664);
    }

    /**
     * return connection
     *
     * @return Connection
     * @throws IOException IOException
     */
    protected Connection getConnection() throws IOException {
        return database.getConnection();
    }
}
