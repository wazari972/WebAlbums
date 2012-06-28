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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jnetfs.core.relay.impl.JnetEnv;
import com.jnetfs.core.vfs.VFile;

public final class DB {

    private static final long serialVersionUID = 6104970572622551049L;
    //Database const
    public static final String JNET_DB_DRIVER = "JnetDBFS.Driver";
    public static final String JNET_DB_URL = "JnetDBFS.URL";
    //connection
    private Connection conn = null;

    /**
     * load from database connection
     *
     * @throws IOException IOException
     */
    public void load() throws IOException {
        conn = getConnection();
        try {
            String tableFileter = JnetEnv.getPropoerty("JnetDBFS.Table.Include");
            VFile table = new VFile(VFile.getRoot(), "TABLE");
            table.setMode(0664);
            VFile view = new VFile(VFile.getRoot(), "VIEW");
            view.setMode(0664);
            ResultSet set = conn.getMetaData().getTables(null, null, null, null);
            while (set.next()) {
                String types = set.getString("TABLE_TYPE");
                String name = set.getString("TABLE_NAME");
                if (name.indexOf('/') != -1
                        || name.indexOf(tableFileter) == -1) {
                    continue;
                }
                if ("TABLE".equals(types)) {
                    new Table(this, table, name);
                } else if ("VIEW".equals(types)) {
                    new Table(this, view, name);
                }
            }
            set.close();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * return connection
     *
     * @return Database connection
     * @throws IOException IOException
     */
    public Connection getConnection() throws IOException {
        if (test()) {
            return conn;
        }
        try {
            Class.forName(JnetEnv.getPropoerty(JNET_DB_DRIVER));
            conn = DriverManager.getConnection(JnetEnv.getPropoerty(JNET_DB_URL));
        } catch (Exception ex) {
            throw new IOException(ex);
        }
        return conn;
    }

    /**
     * close this
     */
    public void unload() {
        try {
            VFile.getRoot().removeAll();
        } catch (IOException ex) {
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
        }
        conn = null;
    }

    /**
     * test connection's validation
     *
     * @return true/false
     */
    private boolean test() {
        try {
            conn.setAutoCommit(true);
            conn.rollback();
            conn.setAutoCommit(false);
            return true;
        } catch (Exception ex) {
            try {
                if (conn != null) {
                    conn.close();
                }
                conn = null;
            } catch (Exception ex1) {
            }
            return false;
        }
    }
}
