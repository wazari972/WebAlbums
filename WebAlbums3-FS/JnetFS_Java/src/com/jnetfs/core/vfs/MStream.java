/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.vfs;

import java.io.IOException;

public class MStream extends VStream {

    private static final long serialVersionUID = 3311907442082603608L;
    //data
    protected byte[] data = null;
    //position
    protected int pos = 0;
    //recall memory
    protected boolean recycle = true;

    /**
     * constructor
     *
     * @param parent vfile
     * @param name name
     */
    public MStream(VFile parent, String name) throws IOException {
        super(parent, name);
    }

    /**
     * set position of vstream
     *
     * @param pos position
     * @throws IOException IOException
     */
    public synchronized void seek(int pos) throws IOException {
        if (pos < 0) {
            throw new IllegalArgumentException();
        }
        this.pos = pos;
        if (this.pos > this.size) {
            this.pos = this.size;
        }
    }

    /**
     * return a byte
     *
     * @return -1 or value
     * @throws IOException IOException
     */
    public synchronized int read() throws IOException {
        if (!isLoaded()) {
            lazyLoad();
        }
        if (this.pos >= this.size) {
            return -1;
        }
        if (this.pos >= data.length) {
            this.pos++;
            return 0;
        }
        int r = data[this.pos++] & 0xff;
        return r;
    }

    /**
     * read data to buffer
     *
     * @param buf buffer
     * @param pos position
     * @param len length
     * @return size of data read or -1 for no more data
     * @throws IOException IOException
     */
    public synchronized int read(byte[] buf, int pos, int len) throws IOException {
        if (!isLoaded()) {
            lazyLoad();
        }
        int left = available();
        if (left == 0) {
            return -1;
        }
        int r = len;
        if (r > left) {
            r = left;
        }
        System.arraycopy(this.data, this.pos, buf, pos, r);
        this.pos += r;
        return r;
    }

    /**
     * write data to vstream
     *
     * @param byte
     * @throws IOException Exception
     */
    public synchronized void write(int b) throws IOException {
        if (!isLoaded()) {
            lazyLoad();
        }
        if (data == null || this.pos >= data.length) {
            enlarge();
        }
        data[this.pos++] = (byte) b;
        if (this.pos > this.size) {
            super.size(this.pos);
        }
        touch();
    }

    /**
     * write buffer to vstream
     *
     * @param buf buffer
     * @param pos position
     * @param len length
     * @throws IOException IOException
     */
    public synchronized void write(byte[] buf, int pos, int len) throws IOException {
        if (!isLoaded()) {
            lazyLoad();
        }
        while (data == null || this.pos + len >= data.length) {
            enlarge();
        }
        System.arraycopy(buf, pos, this.data, this.pos, len);
        this.pos += len;
        if (this.pos > this.size) {
            super.size(this.pos);
        }
        touch();
    }

    /**
     * return the position of vstream
     *
     * @return position
     * @throws IOException IOException
     */
    public synchronized int position() throws IOException {
        return this.pos;
    }

    /**
     * recycle memory
     *
     * @param cycle
     */
    public void recycle(boolean cycle) {
        this.recycle = cycle;
    }

    /**
     * set the length of vstream
     *
     * @param len length
     * @throws IOException IOException
     */
    public synchronized void size(int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException();
        }
        if (this.size != len) {
            if (recycle && data != null && len > SECTOR
                    && len < data.length && data.length >> 1 > len) {
                shrink();
            }
            super.size(len);
            if (this.size < this.pos) {
                this.pos = this.size;
            }
            touch();
        }
    }

    /**
     * free memory
     *
     * @throws IOException IOException
     */
    public synchronized void reset() throws IOException {
        super.size(0);
        this.pos = 0;
        if (!isUpdated()) {
            this.data = null;
            this.data = allocate(SECTOR);
            super.reset();
        }
    }

    /**
     * enlarge memory
     *
     * @throws IOException IOException
     */
    protected synchronized void enlarge() throws IOException {
        if (data == null) {
            data = allocate(SECTOR);
        } else {
            byte[] temp = allocate(data.length << 1);
            System.arraycopy(data, 0, temp, 0, this.pos);
            data = temp;
        }
    }

    /**
     * shrink memory
     *
     * @throws IOException IOException
     */
    protected synchronized void shrink() throws IOException {
        if (data != null) {
            byte[] temp = allocate(data.length >> 1);
            System.arraycopy(data, 0, temp, 0, this.pos);
            data = temp;
        }
    }

    /**
     * get new memory
     *
     * @param len length
     * @return memory
     */
    protected final synchronized byte[] allocate(int len) throws IOException {
        OutOfMemoryError e = new OutOfMemoryError();
        try {
            return new byte[len];
        } catch (OutOfMemoryError ex) {
            throw e;
        }
    }
}