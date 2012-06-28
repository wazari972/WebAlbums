/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.io;

import java.io.IOException;
import java.io.InputStream;

import com.jnetfs.core.Code;
import com.jnetfs.core.relay.IResponse;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.JnetFSImpl;
import com.jnetfs.core.relay.impl.JnetFlush;
import com.jnetfs.core.relay.impl.JnetOpen;
import com.jnetfs.core.relay.impl.JnetRead;
import com.jnetfs.core.relay.impl.JnetRelease;
import com.jnetfs.core.relay.impl.RequestImpl;

public class JnetInputStream extends InputStream {

    private JnetFile file = null;
    private byte[] buff = new byte[4096];
    private int pos = 0;
    private int end = 0;
    private boolean eof = false;
    private long offset = 0;

    /**
     * default constructor
     *
     * @param file JnetFile
     */
    public JnetInputStream(JnetFile file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException();
        }
        this.file = file.readLink();
        JnetJNIConnector jniEnv = this.file.context.getJniEnv();
        jniEnv.setLong(".flags", Code.O_RDONLY);
        jniEnv.setString(JnetFSImpl.PATH, file.getFullPath(this.file.getPath()));
        IResponse r = JnetOpen.instance.operate(RequestImpl.getInstance(jniEnv));
        if (!r.isOK()) {
            throw new IOException("error:" + r.getErrCode());
        }
    }

    /**
     * Returns an estimate of the number of bytes that can be read (or skipped
     * over) from this input stream without blocking by the next invocation of a
     * method for this input stream. The next invocation might be the same
     * thread or another thread. A single read or skip of this many bytes will
     * not block, but may read or skip fewer bytes.
     *
     * <p> Note that while some implementations of {@code InputStream} will
     * return the total number of bytes in the stream, many will not. It is
     * never correct to use the return value of this method to allocate a buffer
     * intended to hold all data in this stream.
     *
     * <p> A subclass' implementation of this method may choose to throw an
     * {@link IOException} if this input stream has been closed by invoking the {@link #close()}
     * method.
     *
     * <p> The {@code available} method for class {@code InputStream} always
     * returns {@code 0}.
     *
     * <p> This method should be overridden by subclasses.
     *
     * @return an estimate of the number of bytes that can be read (or skipped
     * over) from this input stream without blocking or {@code 0} when it
     * reaches the end of the input stream.
     * @exception IOException if an I/O error occurs.
     */
    public int available() throws IOException {
        return end - pos;
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an
     * <code>int</code> in the range
     * <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value
     * <code>-1</code> is returned. This method blocks until input data is
     * available, the end of the stream is detected, or an exception is thrown.
     *
     * <p> A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or
     * <code>-1</code> if the end of the stream is reached.
     * @exception IOException if an I/O error occurs.
     */
    public int read() throws IOException {
        int rest = end - pos;
        if (rest == 0) {
            JnetJNIConnector jniEnv = file.context.getJniEnv();
            jniEnv.setLong("size", buff.length);
            jniEnv.setLong("offset", offset);
            jniEnv.setString(JnetFSImpl.PATH, file.getFullPath(file.getPath()));
            IResponse r = JnetRead.instance.operate(RequestImpl.getInstance(jniEnv));
            int len = r.getErrCode();
            if (len < 0) {
                throw new IOException("error:" + r.getErrCode());
            }
            if (len == 0) {
                return -1;
            }
            byte[] b = r.getConnector().getBytes("buffer");
            if (b != null) {
                System.arraycopy(b, 0, buff, 0, len);
                end = len;
                pos = 0;
                eof = (end == pos);
            }
        }
        if (eof) {
            return -1;
        }
        offset++;
        return buff[pos++] & 0xff;
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     *
     * <p> The
     * <code>close</code> method of
     * <code>InputStream</code> does nothing.
     *
     * @exception IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        JnetJNIConnector jniEnv = file.context.getJniEnv();
        jniEnv.setString(JnetFSImpl.PATH, file.getFullPath(file.getPath()));
        IResponse r = JnetFlush.instance.operate(RequestImpl.getInstance(jniEnv));
        if (!r.isOK()) {
            throw new IOException("error:" + r.getErrCode());
        }
        jniEnv = file.context.getJniEnv();
        jniEnv.setString(JnetFSImpl.PATH, file.getFullPath(file.getPath()));
        r = JnetRelease.instance.operate(RequestImpl.getInstance(jniEnv));
        if (!r.isOK()) {
            throw new IOException("error:" + r.getErrCode());
        }
    }
}