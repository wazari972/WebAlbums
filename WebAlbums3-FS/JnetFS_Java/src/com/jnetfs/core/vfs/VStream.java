/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class VStream extends VFile {

    private static final long serialVersionUID = -1326776838348071969L;
    //the sector size
    public static final int SECTOR = 1024;

    /**
     * default constract for VString file
     *
     * @param parent VFile
     * @param name name
     */
    public VStream(VFile parent, String name) throws IOException {
        super(parent, name, false);
    }

    /**
     * Save to real file
     *
     * @param real file
     * @throws IOException IOException
     */
    protected synchronized void saveOut(File real) throws IOException {
        int pos = this.position();
        try {
            OutputStream os = new FileOutputStream(real);
            byte buff[] = new byte[SECTOR];
            while (true) {
                int c = read(buff);
                if (c == -1) {
                    break;
                }
                os.write(buff, 0, c);
            }
            os.close();
        } finally {
            seek(pos);
        }
    }

    /**
     * load file to memory
     *
     * @param is InputStream
     * @throws IOException IOException
     */
    public synchronized void load(InputStream is) throws IOException {
        byte[] buff = new byte[SECTOR];
        try {
            size(0);
            while (true) {
                int c = is.read(buff);
                if (c == -1) {
                    break;
                }
                write(buff, 0, c);
            }
            setLoaded(true);
        } catch (IOException ex) {
            setLoaded(false);
            throw ex;
        } finally {
            seek(0);
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    /**
     * lazyLoad file to memory
     *
     * @throws IOException IOException
     */
    protected synchronized void lazyLoad() throws IOException {
        if (file != null) {
            load(new FileInputStream(file));
        }
    }

    /**
     * set position of vstream
     *
     * @param pos position
     * @throws IOException IOException
     */
    public synchronized void seek(int pos) throws IOException {
        throw new IOException();
    }

    /**
     * return a byte
     *
     * @return -1 or value
     * @throws IOException IOException
     */
    public synchronized int read() throws IOException {
        throw new IOException();
    }

    /**
     * read into buffer
     *
     * @param buf byte[]
     * @return size of data read or -1 for no more data
     * @throws IOException IOException
     */
    public synchronized int read(byte[] buf) throws IOException {
        return read(buf, 0, buf.length);
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
        for (int i = 0; i < len; i++) {
            int c = read();
            if (c == -1) {
                return (i == 0 ? -1 : i);
            }
            buf[i] = (byte) c;
        }
        return len;
    }

    /**
     * write data to vstream
     *
     * @param byte
     * @throws IOException Exception
     */
    public synchronized void write(int b) throws IOException {
        throw new IOException();
    }

    /**
     * write buffer to vstream
     *
     * @param buf buffer
     * @throws IOException Exception
     */
    public synchronized void write(byte[] buf) throws IOException {
        write(buf, 0, buf.length);
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
        for (int i = 0; i < len; i++) {
            write(buf[pos + i]);
        }
    }

    /**
     * return the position of vstream
     *
     * @return position
     * @throws IOException IOException
     */
    public synchronized int position() throws IOException {
        throw new IOException();
    }

    /**
     * return the available data
     *
     * @return length
     * @throws IOException IOException
     */
    public synchronized int available() throws IOException {
        if (!isLoaded()) {
            lazyLoad();
        }
        return size - position();
    }
}
