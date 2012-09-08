/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.vfs.dbfs.impl;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jnetfs.core.vfs.VFile;

public class Structure extends RStream {

    private static final long serialVersionUID = -1930394008964482645L;
    public static final String NAME = "structure";

    /**
     * constructor
     *
     * @param parent vfile
     * @param name name
     */
    protected Structure(VFile parent) throws IOException {
        super(parent, NAME);
        setMode(0444);
    }

    /**
     * load data
     *
     * @throws IOException IOException
     */
    protected void lazyLoad() throws IOException {
        Statement st = null;
        ResultSet rs = null;
        try {
            st = ((Table) getParent()).getConnection().createStatement();
            String sql = "SELECT * FROM " + getParent().getName() + " WHERE 1=0";
            rs = st.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            StringBuffer buff = new StringBuffer();
            List<String> list = new ArrayList<String>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                rightPad(buff, rsmd.getColumnName(i), 32);
                String val = rsmd.getColumnTypeName(i);
                int p = rsmd.getPrecision(i);
                int s = rsmd.getScale(i);
                if (p != 0 || s != 0) {
                    val += "(";
                    if (p != 0) {
                        val += p;
                    }
                    if (s != 0) {
                        val += "," + s;
                    }
                    val += ")";
                }
                rightPad(buff, val, 18);
                if (rsmd.isNullable(i) == ResultSetMetaData.columnNullable) {
                    buff.append("NULL");
                } else {
                    buff.append("NOT NULL");
                }
                buff.append('\n');
                list.add(buff.toString());
                buff.setLength(0);
            }
            Collections.sort(list);
            buff.append(getParent().getParent().getName()).append(":");
            buff.append(getParent().getName()).append('\n');
            buff.append("==========================================================\n");
            for (String i : list) {
                buff.append(i);
            }
            buff.append("==========================================================\n");
            size(0);
            this.data = allocate(SECTOR);
            setLoaded(true);
            write(buff.toString().getBytes());
            setLoaded(true);
        } catch (SQLException ex) {
            setLoaded(false);
            throw new IOException(ex);
        } finally {
            seek(0);
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    /**
     * right pad
     *
     * @param buff StringBuffer
     * @param str String
     * @param len length
     */
    protected void rightPad(StringBuffer buff, Object str, int len) {
        String s = (str == null ? "" : str.toString());
        buff.append(s);
        for (int i = s.length(); i < len; i++) {
            buff.append(' ');
        }
    }
}