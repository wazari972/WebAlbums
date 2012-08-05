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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OS operation
 *
 * @author jacky
 */
public abstract class JnetFSImpl implements Code {
    
    private static final Logger log = LoggerFactory.getLogger(JnetFSImpl.class.toString());
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
    public static void debug(Object obj) {
        log.warn(obj.toString());
    }
}
