/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.Code;
import java.util.Date;

/**
 * OS operation
 *
 * @author jacky
 */
public abstract class JnetFSImpl implements Code {
    //File system debug

    protected static final boolean debug = JnetEnv.getJnetdebug();
    // File name
    public static final String PATH = "OS_PATH";
    public static final String TO = "OS_PATH_TO";
    public static final String ROOT = "OS_ROOT";
    public static final String JNET_FS = "OS_JNETFS";

    /**
     * Debug output
     *
     * @param obj Object
     */
    protected void debug(Object obj) {
        if (debug) {
            System.out.println(new Date() + "\t" + obj);
        }
    }
}
