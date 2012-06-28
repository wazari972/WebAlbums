/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.io;

import com.jnetfs.core.Code;
import com.jnetfs.core.JnetException;
import com.jnetfs.core.Server;
import com.jnetfs.core.relay.Client;
import com.jnetfs.core.relay.JnetJNIConnector;
import com.jnetfs.core.relay.impl.JnetDestroy;
import com.jnetfs.core.relay.impl.JnetFSAdapter;
import com.jnetfs.core.relay.impl.JnetInit;
import com.jnetfs.core.relay.impl.RequestImpl;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class JnetContext extends Thread {
    // JnetJNIConnector

    private JnetJNIConnector jniEnv = null;
    // class name
    private String fsName = "com.jnetfs.vfs.jfs.JnetJavaFS";
    // JavaNET file system's name
    private String server = null;
    // working port
    private int port = Server.PORT;
    // mount point
    private String hotpoint = null;
    // remove file list
    List<JnetFile> removehook = new LinkedList<JnetFile>();

    /**
     * context class
     *
     * @param server String
     * @param port int
     * @param mountPoint String
     * @param fs Class
     * @throws JnetException
     */
    public JnetContext(String server,
            int port,
            String mountPoint,
            Class<JnetFSAdapter> fs) throws JnetException {
        this.server = server;
        this.port = port;
        this.hotpoint = mountPoint;
        if (fs != null) {
            this.fsName = fs.getName();
        }
        jniEnv = new JnetJNIConnector();
        new Client(this);
        if (!JnetInit.instance.operate(RequestImpl.getInstance(getJniEnv())).isOK()) {
            throw new JnetException(Code.EACCES, "Cann't connect to server!");
        }
        Runtime.getRuntime().addShutdownHook(this);
    }

    /**
     * context class
     *
     * @param server String
     * @throws JnetException
     */
    public JnetContext(String server) throws JnetException {
        this(server, Server.PORT, "/", null);
    }

    /**
     * context class
     *
     * @param server String
     * @param port int
     * @throws JnetException
     */
    public JnetContext(String server, int port) throws JnetException {
        this(server, port, "/", null);
    }

    /**
     * return a Client.Communication
     *
     * @return Client.Communication
     */
    public Client.Communication CreateComm() {
        System.setProperty("jnet.fs." + 0, fsName);
        System.setProperty("jnet.path." + 0, hotpoint);
        System.setProperty("jnet.localfs." + 0, String.valueOf(server == null));
        if (server == null) {
            server = "localhost." + 0;
        }
        System.setProperty("jnet.ip." + 0, server);
        System.setProperty("jnet.port." + 0, String.valueOf(port));
        return new Client.Communication(server, String.valueOf(port), 0);
    }

    /**
     * return JnetJNIConnector
     *
     * @return JnetJNIConnector
     */
    public JnetJNIConnector getJniEnv() {
        jniEnv.reset();
        return jniEnv;
    }

    /**
     * When an object implementing interface
     * <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's
     * <code>run</code> method to be called in that separately executing thread.
     * <p> The general contract of the method
     * <code>run</code> is that it may take any action whatsoever.
     *
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        for (JnetFile f : removehook) {
            try {
                f.delete();
            } catch (IOException ex) {
            }
        }
        JnetDestroy.instance.operate(RequestImpl.getInstance(getJniEnv()));
    }
}
