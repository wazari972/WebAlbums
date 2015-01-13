/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.Code;
import com.jnetfs.core.JnetException;
import com.jnetfs.core.relay.JnetJNIConnector;

public class JnetFSAdapter {    
    public int hello(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ESUCCESS;
    }

    public int init(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ESUCCESS;
    }

    public int destroy(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ESUCCESS;
    }

    public int chmod(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ESUCCESS;
    }

    public int attributes(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int list(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int open(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int read(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int write(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int release(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int truncate(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int flush(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int create(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int mkdir(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int delete(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int rmdir(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int rename(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int touch(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int statfs(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int symlink(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }

    public int readlink(JnetJNIConnector jniEnv) throws JnetException {
        return Code.ENOSYS;
    }
}
