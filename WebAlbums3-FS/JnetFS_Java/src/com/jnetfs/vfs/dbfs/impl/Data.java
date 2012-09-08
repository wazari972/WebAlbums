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

public class Data extends RStream {

    private static final long serialVersionUID = 8325332579543205791L;
    public static final String NAME = "data.csv";

    /**
     * constructor
     *
     * @param parent vfile
     */
    protected Data(VFile parent) throws IOException {
        this(parent, NAME);
        new Filter(parent);
        setMode(0444);
    }

    /**
     * constructor
     *
     * @param parent VFile
     * @param name name
     * @throws IOException IOException
     */
    public Data(VFile parent, String name) throws IOException {
        super(parent, name);
    }

    /**
     * load data
     *
     * @throws IOException IOException
     */
    protected void lazyLoad() throws IOException {
        Statement st = null;
        ResultSet rs = null;
        String sql = "";
        try {
            st = ((Table) getParent()).getConnection().createStatement();
            sql = "SELECT * FROM " + getParent().getName() + " " + getWhere();
            rs = st.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            List<String> list = new ArrayList<String>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                list.add(rsmd.getColumnName(i));
            }
            Collections.sort(list);
            StringBuffer buff = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    buff.append('\t');
                }
                buff.append(list.get(i));
            }
            size(0);
            this.data = allocate(SECTOR);
            buff.append('\n');
            setLoaded(true);
            write(buff.toString().getBytes());
            while (rs.next()) {
                buff.setLength(0);
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        buff.append('\t');
                    }
                    Object obj = rs.getObject(list.get(i));
                    if (obj == null) {
                        buff.append("<NULL>");
                    } else {
                        buff.append(obj);
                    }
                }
                setLoaded(true);
                buff.append('\n');
                write(buff.toString().getBytes());
                buff.setLength(0);
            }
            setLoaded(true);
        } catch (SQLException ex) {
            setLoaded(false);
            throw new IOException(sql);
        } finally {
            seek(0);
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    /**
     * return where cause
     *
     * @return String
     * @throws IOException
     */
    protected String getWhere() throws IOException {
        Filter filter = (Filter) VFile.find(getParent(), Filter.NAME);
        filter.seek(0);
        if (filter.available() > 0) {
            StringBuffer buff = new StringBuffer();
            while (filter.available() > 0) {
                buff.append((char) filter.read());
            }
            String r = buff.toString().trim();
            if (r.length() > 0) {
                r = "WHERE " + r;
            }
            filter.seek(0);
            return r;
        } else {
            return "";
        }
    }
}