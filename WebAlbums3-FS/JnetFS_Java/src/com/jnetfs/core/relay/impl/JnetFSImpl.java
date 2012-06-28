/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import java.lang.reflect.Method;
import java.util.Date;

import com.jnetfs.core.Code;
import com.jnetfs.core.JnetException;
import com.jnetfs.core.relay.IOperate;
import com.jnetfs.core.relay.IRequest;
import com.jnetfs.core.relay.IResponse;
import com.jnetfs.core.relay.JnetJNIConnector;

/**
 * OS operation
 *
 * @author jacky
 */
public abstract class JnetFSImpl implements IOperate, Code {
    //File system debug

    protected static final boolean debug = JnetEnv.getJnetdebug();
    // File name
    public static final String PATH = "OS_PATH";
    public static final String TO = "OS_PATH_TO";
    public static final String ROOT = "OS_ROOT";
    public static final String JNET_FS = "OS_JNETFS";

    /**
     * I/O operate
     *
     * @param request IRequest
     * @return IResponse
     */
    public final synchronized IResponse operate(IRequest request) {
        String cmd = request.getCommand();
        int r = 0;
        try {
            checkRights(request);
            Method method = getClass().getMethod(cmd, JnetJNIConnector.class);
            r = (Integer) method.invoke(this, request.getConnector());
            method = null;
        } catch (Throwable ex) {
            if (ex instanceof JnetException) {
                r = ((JnetException) ex).getCode();
            } else {
                r = EIO;
            }
        }
        return ResponseImpl.getInstance(r, request.getConnector());
    }

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

    /**
     * rights check
     *
     * @param request IRequest
     * @throws JnetException JnetException
     */
    public abstract void checkRights(IRequest request) throws JnetException;
}
