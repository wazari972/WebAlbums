/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.io;

import java.io.IOException;
import java.io.OutputStream;

import com.jnetfs.core.Code;
import com.jnetfs.core.relay.IResponse;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.JnetCreate;
import com.jnetfs.core.relay.impl.JnetFSImpl;
import com.jnetfs.core.relay.impl.JnetFlush;
import com.jnetfs.core.relay.impl.JnetOpen;
import com.jnetfs.core.relay.impl.JnetRelease;
import com.jnetfs.core.relay.impl.JnetTruncate;
import com.jnetfs.core.relay.impl.JnetWrite;
import com.jnetfs.core.relay.impl.RequestImpl;

public class JnetOutputStream extends OutputStream {

    private JnetFile file = null;
    private byte[] buff = new byte[4096];
    private int pos = 0;
    private long offset = 0;

    /**
     * default constructor
     *
     * @param file JnetFile
     */
    public JnetOutputStream(JnetFile file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException();
        }
        this.file = file.readLink();
        if (file.exists()) {
            JnetJNIConnector jniEnv = this.file.context.getJniEnv();
            jniEnv.setLong("offset", 0l);
            jniEnv.setString(JnetFSImpl.PATH, this.file.getFullPath(this.file.getPath()));
            IResponse r = JnetTruncate.instance.operate(RequestImpl.getInstance(jniEnv));
            if (!r.isOK()) {
                throw new IOException("error:" + r.getErrCode());
            }
        } else {
            JnetJNIConnector jniEnv = this.file.context.getJniEnv();
            jniEnv.setString(JnetFSImpl.PATH, this.file.getFullPath(this.file.getPath()));
            IResponse r = JnetCreate.instance.operate(RequestImpl.getInstance(jniEnv));
            if (!r.isOK()) {
                throw new IOException("error:" + r.getErrCode());
            }
        }
        JnetJNIConnector jniEnv = this.file.context.getJniEnv();
        jniEnv.setLong(".flags", Code.O_WRONLY);
        jniEnv.setString(JnetFSImpl.PATH, this.file.getFullPath(this.file.getPath()));
        IResponse r = JnetOpen.instance.operate(RequestImpl.getInstance(jniEnv));
        if (!r.isOK()) {
            throw new IOException("error:" + r.getErrCode());
        }
    }

    /**
     * Writes the specified byte to this output stream. The general contract for
     * <code>write</code> is that one byte is written to the output stream. The
     * byte to be written is the eight low-order bits of the argument
     * <code>b</code>. The 24 high-order bits of
     * <code>b</code> are ignored. <p> Subclasses of
     * <code>OutputStream</code> must provide an implementation for this method.
     *
     * @param b the
     * <code>byte</code>.
     * @exception IOException if an I/O error occurs. In particular, an
     * <code>IOException</code> may be thrown if the output stream has been
     * closed.
     */
    public void write(int b) throws IOException {
        if (pos == buff.length) {
            flush();
        }
        buff[pos++] = (byte) b;
        offset++;
    }

    /**
     * Flushes this output stream and forces any buffered output bytes to be
     * written out. The general contract of
     * <code>flush</code> is that calling it is an indication that, if any bytes
     * previously written have been buffered by the implementation of the output
     * stream, such bytes should immediately be written to their intended
     * destination. <p> If the intended destination of this stream is an
     * abstraction provided by the underlying operating system, for example a
     * file, then flushing the stream guarantees only that bytes previously
     * written to the stream are passed to the operating system for writing; it
     * does not guarantee that they are actually written to a physical device
     * such as a disk drive. <p> The
     * <code>flush</code> method of
     * <code>OutputStream</code> does nothing.
     *
     * @exception IOException if an I/O error occurs.
     */
    public void flush() throws IOException {
        if (pos > 0) {
            JnetJNIConnector jniEnv = file.context.getJniEnv();
            jniEnv.setLong("size", pos);
            jniEnv.setLong("offset", offset - pos);
            if (pos < buff.length) {
                byte[] b = new byte[pos];
                System.arraycopy(buff, 0, b, 0, pos);
                jniEnv.setBytes("buffer", b);
            } else {
                jniEnv.setBytes("buffer", buff);
            }
            jniEnv.setString(JnetFSImpl.PATH, file.getFullPath(file.getPath()));
            IResponse r = JnetWrite.instance.operate(RequestImpl.getInstance(jniEnv));
            if (r.getErrCode() < 0) {
                throw new IOException("error:" + r.getErrCode());
            }
            pos = 0;
        }
    }

    /**
     * Closes this output stream and releases any system resources associated
     * with this stream. The general contract of
     * <code>close</code> is that it closes the output stream. A closed stream
     * cannot perform output operations and cannot be reopened. <p> The
     * <code>close</code> method of
     * <code>OutputStream</code> does nothing.
     *
     * @exception IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        flush();
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
