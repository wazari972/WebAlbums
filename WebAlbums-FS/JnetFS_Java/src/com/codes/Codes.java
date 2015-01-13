/**
 * Copyright (C) 2009 Jacky WU (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.codes;

public class Codes {

    static int MAX_LEN = 256;

    public static byte[] init() {
        byte[] r = new byte[MAX_LEN];
        for (int i = 0; i < MAX_LEN; i++) {
            r[i] = (byte) (MAX_LEN - 1 - i);
        }
        return r;
    }

    public static int next(byte[] seed) {
        while (true) {
            int shift = (int) (Math.random() * MAX_LEN);
            if (shift == 0 || seed[MAX_LEN - shift] == 0) {
                continue;
            }
            return shift;
        }
    }

    public static byte[] shift(byte[] seed, int shift) {
        byte[] t = new byte[shift];
        System.arraycopy(seed, MAX_LEN - shift, t, 0, shift);
        System.arraycopy(seed, 0, seed, shift, MAX_LEN - shift);
        System.arraycopy(t, 0, seed, 0, shift);
        return seed;
    }

    public static int encode(byte[] seed, int value) {
        return seed[value & 0xff] & 0xff;
    }

    public static int decode(byte[] seed, int value) {
        return encode(seed, value);
    }
}
