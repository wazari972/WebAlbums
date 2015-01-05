/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay;

import com.jnetfs.core.JnetException;
import com.jnetfs.core.JnetFS;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class JnetJNIConnector implements Serializable {

    private static final long serialVersionUID = -6247180021174967890L;
    //connector attributes
    private Map<String, Object> attrs = null;

    /**
     * default constructor
     */
    public JnetJNIConnector() {
        this.attrs = new HashMap<>();
    }

    /**
     * return a string value
     *
     * @param key keys
     * @return String
     * @throws JnetException
     */
    public String getString(String key) throws JnetException {
        Object obj = attrs.get(key);
        if (obj instanceof String) {
            return (String) obj;
        }
        throw new JnetException(-JnetFS.EIO, "Bad parameter");
    }

    /**
     * return a integer value
     *
     * @param key String
     * @return Integer
     * @throws JnetException
     */
    public Integer getInteger(String key) throws JnetException {
        Object obj = attrs.get(key);
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        throw new JnetException(JnetFS.EIO, "Bad parameter");
    }

    /**
     * get a long value
     *
     * @param key String
     * @return
     * @throws JnetException
     */
    public Long getLong(String key) throws JnetException {
        Object obj = attrs.get(key);
        if (obj instanceof Long) {
            return (Long) obj;
        }
        throw new JnetException(JnetFS.EIO, "Bad parameter");
    }

    /**
     * get binary from jni
     *
     * @param key String
     * @return byte[]
     * @throws JnetException
     */
    public byte[] getBytes(String key) throws JnetException {
        Object obj = attrs.get(key);
        if (obj instanceof byte[]) {
            return (byte[]) obj;
        }
        throw new JnetException(JnetFS.EIO, "Bad parameter");
    }

    /**
     * set integer value to jni
     *
     * @param key String
     * @param val int
     */
    public void setInteger(String key, int val) {
        attrs.put(key, val);
    }

    /**
     * set long value to jni
     *
     * @param key string
     * @param val long
     */
    public void setLong(String key, long val) {
        attrs.put(key, val);
    }

    /**
     * set string value
     *
     * @param key String
     * @param val String
     */
    public void setString(String key, String val) {
        attrs.put(key, val);
    }

    /**
     * set binary to jin
     *
     * @param key String
     * @param val val
     */
    public void setBytes(String key, byte[] val) {
        attrs.put(key, val);
    }

    /**
     * setup values
     *
     * @param key String
     * @param val Object
     */
    public void setObject(String key, Object val) {
        if (val == null
                || val instanceof Long
                || val instanceof Integer
                || val instanceof String
                || val instanceof byte[]) {
            attrs.put(key, val);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * return object
     *
     * @param key string
     * @return object
     */
    public Object getObject(String key) {
        if (attrs.containsKey(key)) {
            return attrs.get(key);
        }
        throw new IllegalArgumentException();
    }

    /**
     * write data to stream
     *
     * @param stream stream
     * @throws IOException IOException
     */
    public void write(DataOutputStream stream) throws IOException {
        stream.writeInt(attrs.size());
        for (String key : attrs.keySet()) {
            stream.writeUTF(key);
            Object obj = attrs.get(key);
            if (obj == null) {
                stream.writeByte(0); //null
            } else if (obj instanceof byte[]) {
                byte[] b = (byte[]) obj;
                stream.writeByte(1); //byte
                stream.writeInt(b.length);
                stream.write(b);
            } else if (obj instanceof Long) {
                Long l = (Long) obj;
                stream.writeByte(2); //long
                stream.writeLong(l);
            } else if (obj instanceof Integer) {
                Integer i = (Integer) obj;
                stream.writeByte(3); //Integer
                stream.writeInt(i);
            } else if (obj instanceof String) {
                String s = (String) obj;
                stream.writeByte(4); //String
                stream.writeUTF(s);
            }
        }
    }

    /**
     * read object from stream
     *
     * @param stream DataInputStream
     * @throws IOException IOException
     */
    public void read(DataInputStream stream) throws IOException {
        int size = stream.readInt();
        for (int i = 0; i < size; i++) {
            String key = stream.readUTF();
            switch (stream.readByte()) {
                case 0: // null
                    attrs.put(key, null);
                    break;
                case 1: // bytes
                    int len = stream.readInt();
                    byte[] b = new byte[len];
                    stream.readFully(b);
                    attrs.put(key, b);
                    break;
                case 2: // long
                    attrs.put(key, stream.readLong());
                    break;
                case 3: // integer
                    attrs.put(key, stream.readInt());
                    break;
                case 4: // String
                    attrs.put(key, stream.readUTF());
                    break;
            }
        }
    }

    /**
     * reset container
     */
    public void reset() {
        attrs.clear();
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that "textually represents"
     * this object. The result should be a concise but informative
     * representation that is easy for a person to read. It is recommended that
     * all subclasses override this method. <p> The
     * <code>toString</code> method for class
     * <code>Object</code> returns a string consisting of the name of the class
     * of which the object is an instance, the at-sign character `
     * <code>@</code>', and the unsigned hexadecimal representation of the hash
     * code of the object. In other words, this method returns a string equal to
     * the value of: <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "JniConnector:" + attrs;
    }
}