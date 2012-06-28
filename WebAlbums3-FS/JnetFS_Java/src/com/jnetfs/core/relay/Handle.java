/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core.relay;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.codes.SecretInputStream;
import com.codes.SecretOutputStream;
import com.jnetfs.core.relay.impl.JnetFSImpl;
import com.jnetfs.core.relay.impl.RequestImpl;

/**
 * Thread handle, allow mult-user access the server
 *
 * @author jacky
 */
public class Handle extends Thread {
    //fs cache

    private final static Map<String, JnetFSImpl> fscache = new HashMap<String, JnetFSImpl>();
    // client socket
    private Socket socket = null;

    /**
     * default constructor
     *
     * @param socket Socket
     */
    public Handle(Socket socket) {
        this.socket = socket;
    }

    /**
     * If this thread was constructed using a separate
     * <code>Runnable</code> run object, then that
     * <code>Runnable</code> object's
     * <code>run</code> method is called; otherwise, this method does nothing
     * and returns. <p> Subclasses of
     * <code>Thread</code> should override this method.
     *
     * @see #start()
     * @see #stop()
     * @see #Thread(ThreadGroup, Runnable, String)
     */
    public void run() {
        try {
            socket.setTcpNoDelay(true);
            DataInputStream inChanel = new DataInputStream(
                    new SecretInputStream(new BufferedInputStream(socket.getInputStream())));
            DataOutputStream outChanel = new DataOutputStream(
                    new SecretOutputStream(new BufferedOutputStream(socket.getOutputStream())));
            JnetJNIConnector connector = new JnetJNIConnector();
            while (true) {
                //receive request
                connector.read(inChanel);
                //get command
                String cmd = connector.getString("command");
                IRequest request = RequestImpl.getInstance(cmd, connector);
                //execute
                IResponse response = getJnetFSImpl(request).operate(request);
                connector.setInteger("error.code", response.getErrCode());
                //send out
                connector.write(outChanel);
                outChanel.flush();
                //reset connector
                connector.reset();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
            }
        }
    }

    /**
     * return a Jnet FS Implement
     *
     * @param request
     * @return
     * @throws Exception
     */
    public static JnetFSImpl getJnetFSImpl(IRequest request) throws Exception {
        String jnetfs = (String) request.getConnector().getString(JnetFSImpl.JNET_FS);
        synchronized (fscache) {
            JnetFSImpl r = fscache.get(jnetfs);
            if (r != null) {
                return r;
            }
            r = (JnetFSImpl) Class.forName(jnetfs).newInstance();
            fscache.put(jnetfs, r);
            return r;
        }
    }
}
