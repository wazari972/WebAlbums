/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay.impl;

import com.jnetfs.core.relay.JnetJNIConnector;
import java.io.InputStream;
import java.util.Properties;

/**
 * Jnet environment
 *
 * @author jacky
 */
public class JnetEnv {

    /**
     * Thread safe access
     */
    private static ThreadLocal<Properties> properties = new ThreadLocal<Properties>();

    /**
     * return server ip address
     *
     * @return address
     */
    public static String getServerIP() {
        return getServerIP(0);
    }

    /**
     * get server ip by pos
     *
     * @param pos int
     * @return string
     */
    public static String getServerIP(int pos) {
        return load().getProperty("jnet.ip." + pos);
    }

    /**
     * return server port
     *
     * @return port
     */
    public static String getServerPort() {
        return getServerPort(0);
    }

    /**
     * return server port
     *
     * @param pos int
     * @return String
     */
    public static String getServerPort(int pos) {
        return load().getProperty("jnet.port." + pos);
    }

    /**
     * get server root directory
     *
     * @return root path
     */
    public static String getJnetRoot() {
        return getJnetRoot(0);
    }

    /**
     * return root
     *
     * @param pos int
     * @return String
     */
    public static String getJnetRoot(int pos) {
        String r = load().getProperty("jnet.path." + pos);
        if (r == null) {
            r = System.getProperty("user.dir");
        }
        return r;
    }

    /**
     * return jnet user
     *
     * @return user
     */
    public static String getJnetUser() {
        return load().getProperty("jnet.usr");
    }

    /**
     * return password
     *
     * @return password
     */
    public static String getJnetPassword() {
        return load().getProperty("jnet.pwd");
    }

    /**
     * return jnet-fs
     *
     * @return jnet-fs
     */
    public static String getJnetFS() {
        return getJnetFS(0);
    }

    /**
     * return jnetfs class
     *
     * @param pos int
     * @return String
     */
    public static String getJnetFS(int pos) {
        return load().getProperty("jnet.fs." + pos);
    }

    /**
     * local fs usage
     *
     * @return String
     */
    public static boolean getJnetLocalFS() {
        return Boolean.valueOf(getJnetLocalFS(0));
    }

    /**
     * local fs usage
     *
     * @return String
     */
    public static boolean getJnetLocalFS(int pos) {
        return Boolean.valueOf(load().getProperty("jnet.localfs." + pos, "false"));
    }

    /**
     * return reconnect time out
     *
     * @return long
     */
    public static long getJetReconnectTimeout() {
        return getJetReconnectTimeout(0);
    }

    /**
     * return reconnect time out
     *
     * @param pos position
     * @return long
     */
    public static long getJetReconnectTimeout(int pos) {
        long r = 30 * 1000; //30 second
        try {
            r = Long.parseLong(load().getProperty("jnet.reconnect.timeout." + pos));
        } catch (Exception ex) {
        }
        return r;
    }

    /**
     * jnet debug
     *
     * @return boolean
     */
    public static boolean getJnetdebug() {
        return Boolean.valueOf(load().getProperty("jnet.debug", "false"));
    }

    /**
     * return default env
     *
     * @return Map<String,Object>
     */
    public static void setDefaultEnv(JnetJNIConnector connector) {
        connector.setString(JnetFSImpl.JNET_FS, getJnetFS());
        connector.setString(JnetFSImpl.ROOT, getJnetRoot());
        connector.setString("user", getJnetUser());
        connector.setString("password", getJnetPassword());
    }

    /**
     * return default env
     *
     * @return Map<String,Object>
     */
    public static void setDefaultEnv(JnetJNIConnector connector, int pos) {
        connector.setString(JnetFSImpl.JNET_FS, getJnetFS(pos));
        connector.setString(JnetFSImpl.ROOT, getJnetRoot(pos));
        connector.setString("user", getJnetUser());
        connector.setString("password", getJnetPassword());
    }

    /**
     * return property
     *
     * @param key String
     * @return String
     */
    public static String getPropoerty(String key) {
        return load().getProperty(key);
    }

    /**
     * load configured properties
     *
     * @return Properties
     */
    protected static Properties load() {
        Properties r = properties.get();
        if (r == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = loader.getResourceAsStream("/jnetfs.properties");
            if (is == null) {
                is = properties.getClass().getResourceAsStream("/jnetfs.properties");
            }
            try {
                r = new Properties();
                if (is != null) {
                    r.load(is);
                }
                r.putAll(System.getProperties());
                properties.set(r);
            } catch (Throwable ex) {
            }
        }
        return r;
    }
}
