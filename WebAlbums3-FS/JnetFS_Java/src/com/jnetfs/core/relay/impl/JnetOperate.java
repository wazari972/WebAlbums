/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.relay.Client;
import com.jnetfs.core.relay.IOperate;
import com.jnetfs.core.relay.IRequest;
import com.jnetfs.core.relay.IResponse;
import com.jnetfs.core.relay.JnetJNIConnector;

/**
 * @author jacky
 */
public class JnetOperate implements IOperate {

    protected String command;

    public JnetOperate(String command) {
        this.command = command;
    }

    /**
     * I/O operate
     *
     * @param request IRequest
     * @return IResponse
     */
    public IResponse operate(IRequest request) {
        JnetJNIConnector conn = request.getConnector();
        conn.setString("command", command);
        return Client.getInstance().operate(RequestImpl.getInstance(command, conn));
    }
}
