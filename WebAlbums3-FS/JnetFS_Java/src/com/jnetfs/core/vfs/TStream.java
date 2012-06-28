package com.jnetfs.core.vfs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TStream extends VStream {

    private static final long serialVersionUID = 2413045719179729750L;
    //data
    protected RandomAccessFile data;

    /**
     * constructor
     *
     * @param parent vfile
     * @param name name
     */
    public TStream(VFile parent, String name) throws IOException {
        super(parent, name);
    }

    /**
     * add notify
     *
     * @throws IOException
     */
    protected void addNotify() throws IOException {
        if (data == null) {
            file = File.createTempFile("TStream", null);
            file.deleteOnExit();
            data = new RandomAccessFile(file, "rw");
        }
    }

    /**
     * remove notify
     *
     * @throws IOException
     */
    protected void removeNotify() throws IOException {
        if (data != null) {
            data.close();
        }
        data = null;
        file = null;
    }

    /**
     * set position of vstream
     *
     * @param pos position
     * @throws IOException IOException
     */
    public synchronized void seek(int pos) throws IOException {
        data.seek(pos);
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
        return data.read();
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
        return data.read(buf, pos, len);
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
        data.write(b);
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
        data.write(buf, pos, len);
        touch();
    }

    /**
     * return the position of vstream
     *
     * @return position
     * @throws IOException IOException
     */
    public synchronized int position() throws IOException {
        return (int) data.getFilePointer();
    }

    /**
     * set the length of vstream
     *
     * @param len length
     * @throws IOException IOException
     */
    public synchronized void size(int len) throws IOException {
        data.setLength(len);
        touch();
    }

    /**
     * return this size of vfile
     *
     * @return size
     */
    public synchronized int size() throws IOException {
        if (!isLoaded()) {
            lazyLoad();
        }
        return (int) data.length();
    }
}
