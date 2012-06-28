/**
 * Copyright (C) 2009 Jacky WU (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.codes;

import java.io.IOException;
import java.io.InputStream;

public class SecretInputStream extends InputStream {

    private InputStream is = null;
    private int count = 0;
    private byte[] seed = null;

    public SecretInputStream(InputStream is) {
        this.is = is;
        this.seed = Codes.init();
    }

    @Override
    public int read() throws IOException {
        if (count == 0) {
            int shift = is.read();
            count = is.read();
            if (shift == -1 || count == -1) {
                throw new IOException();
            }
            Codes.shift(seed, Codes.decode(seed, shift));
            count = Codes.decode(seed, count);
        }
        int r = is.read();
        if (r != -1) {
            r = Codes.decode(seed, r);
        }
        count--;
        return r;
    }

    @Override
    public void close() throws IOException {
        is.close();
        is = null;
    }
}
