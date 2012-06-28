/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay;

/**
 * I/O operate interface
 *
 * @author jacky
 */
public interface IOperate {

    /**
     * I/O operate
     *
     * @param request IRequest
     * @return IResponse
     */
    public IResponse operate(IRequest request);
}
