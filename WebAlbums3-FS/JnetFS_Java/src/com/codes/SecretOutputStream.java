/**
 * Copyright (C) 2009 Jacky WU (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.codes;

import java.io.IOException;
import java.io.OutputStream;

public class SecretOutputStream extends OutputStream {

    private OutputStream os = null;
    private int count = 0;
    private byte[] seed = null;

    public SecretOutputStream(OutputStream os) {
        this.os = os;
        this.seed = Codes.init();
    }

    @Override
    public void write(int b) throws IOException {
        if (count == 0) {
            int shift = Codes.next(seed);
            os.write(Codes.encode(seed, shift));
            Codes.shift(seed, shift);
            count = Codes.next(seed);
            os.write(Codes.encode(seed, count));
        }
        os.write(Codes.encode(seed, b));
        count--;
    }

    public void flush() throws IOException {
        os.flush();
    }

    public void close() throws IOException {
        os.close();
        os = null;
    }
}
