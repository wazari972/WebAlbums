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
import com.jnetfs.core.Code;
import com.jnetfs.core.JnetException;
import com.jnetfs.core.Server;
import com.jnetfs.core.io.JnetContext;
import com.jnetfs.core.relay.impl.JnetAttributes;
import com.jnetfs.core.relay.impl.JnetEnv;
import com.jnetfs.core.relay.impl.JnetFSImpl;
import com.jnetfs.core.relay.impl.JnetList;
import com.jnetfs.core.relay.impl.JnetStatfs;
import com.jnetfs.core.relay.impl.ResponseImpl;

/**
 * Client delegation
 *
 * @author jacky
 */
public class Client implements IOperate {

    protected Map<String, Communication> servers = new HashMap<String, Communication>();
    //instance one
    protected static Client instance = null;

    /**
     * default constructor
     */
    public Client() {
        int local = 0;
        for (int i = 0;; i++) {
            String server = JnetEnv.getServerIP(i);
            String port = JnetEnv.getServerPort(i);
            if (JnetEnv.getJnetLocalFS(i)) {
                if (server == null) {
                    server = "localhost." + local++;
                }
            } else if (server == null || port == null) {
                break;
            }
            servers.put(server, new Communication(server, port, i));
        }
    }

    /**
     * default constructor
     *
     * @param context
     */
    public Client(JnetContext context) {
        Communication conn = context.CreateComm();
        servers.put(conn.server, conn);
        if (instance == null) {
            instance = this;
        }
    }

    /**
     * getInstance
     *
     * @return Client
     */
    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    /**
     * I/O operate
     *
     * @param request IRequest
     * @return IResponse
     */
    public synchronized IResponse operate(IRequest request) {
        JnetJNIConnector jniEnv = request.getConnector();
        try {
            String command = request.getCommand();
            //send to each server
            if ("hello".equals(command)
                    || "init".equals(command)
                    || "destroy".equals(command)) {
                boolean r = false;
                for (Communication conn : servers.values()) {
                    JnetEnv.setDefaultEnv(jniEnv, conn.pos);
                    IResponse t = conn.operate(request);
                    if (t.isOK()) {
                        r = true;
                    }
                }
                return r ? ResponseImpl.getInstance(IResponse.ESUCCESS)
                        : ResponseImpl.getInstance(IResponse.EACCES);
            } else {
                String path = jniEnv.getString(JnetFSImpl.PATH);
                if (!"/".equals(path)) {
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    Communication conn = null;
                    if (servers.containsKey(path)) {
                        conn = servers.get(path);
                        path = "/";
                    } else {
                        int idx = 0;
                        if ("symlink".equals(command) || "rename".equals(command)) {
                            String to = jniEnv.getString(JnetFSImpl.TO);
                            for (String name : servers.keySet()) {
                                idx = to.indexOf(name);
                                if (idx != -1) {
                                    to = to.substring(idx);
                                    idx = to.indexOf('/');
                                    if (idx == -1) {
                                        return ResponseImpl.getInstance(IResponse.EACCES);
                                    }
                                    break;
                                }
                            }
                            String server = to.substring(0, idx);
                            to = to.substring(idx);
                            jniEnv.setString(JnetFSImpl.TO, to);
                            conn = servers.get(server);
                            path = jniEnv.getString(JnetFSImpl.PATH);
                            if (path.indexOf(server) != -1) {
                                idx = path.indexOf(server);
                                path = path.substring(idx + server.length());
                            }
                        } else {
                            idx = path.indexOf('/');
                            if (idx == -1) {
                                return ResponseImpl.getInstance(IResponse.EACCES);
                            }
                            String server = path.substring(0, idx);
                            path = path.substring(idx);
                            conn = servers.get(server);
                        }
                    }
                    jniEnv.setString(JnetFSImpl.PATH, path);
                    JnetEnv.setDefaultEnv(jniEnv, conn.pos);
                    return conn.operate(request);
                } else if ("statfs".equals(command)) {
                    long count = 1l << 26;
                    JnetStatfs.setNameMaxLen(jniEnv, 255);
                    JnetStatfs.setBlockSize(jniEnv, 1 << 12);
                    JnetStatfs.setBlocks(jniEnv, count);
                    JnetStatfs.setFreeBlocks(jniEnv, count);
                    JnetStatfs.setAvailableBlocks(jniEnv, count);
                    JnetStatfs.setFreeSize(jniEnv, count);
                    JnetStatfs.setFiles(jniEnv, count);
                    JnetStatfs.setFileFree(jniEnv, count);
                    JnetStatfs.setFileAvailable(jniEnv, count);
                    JnetStatfs.setFileSID(jniEnv, 0);
                } else if ("attributes".equals(command)) {
                    int st_mode = Code.S_IFDIR | 0755;
                    long st_nlink = 1;
                    long st_mtim = System.currentTimeMillis() / 1000l;
                    long st_size = 1l << 12;
                    JnetAttributes.setMode(jniEnv, st_mode);
                    JnetAttributes.setTime(jniEnv, st_mtim);
                    JnetAttributes.setLinks(jniEnv, st_nlink);
                    JnetAttributes.setSize(jniEnv, st_size);
                } else if ("list".equals(command)) {
                    int count = 0;
                    for (String name : servers.keySet()) {
                        JnetList.addName(jniEnv, count, name);
                        count++;
                    }
                    JnetList.setCount(jniEnv, count);
                } else {
                    return ResponseImpl.getInstance(IResponse.EACCES);
                }
            }
            return ResponseImpl.getInstance(IResponse.ESUCCESS);
        } catch (JnetException ex) {
            return ResponseImpl.getInstance(IResponse.EACCES);
        }
    }

    /**
     * communication object
     */
    public static class Communication {
        // socket object

        private Socket socket = null;
        // output stream
        private DataOutputStream outChanel = null;
        // input stream
        private DataInputStream inChanel = null;
        // server ports
        private String server = null;
        // port
        private String port = null;
        // position
        private int pos = 0;
        // fail connection
        private boolean broken = false;
        private long tryTime = 0l;

        /**
         * constructor
         *
         * @param server String
         * @param port String
         * @param pos position
         */
        public Communication(String server, String port, int pos) {
            this.server = server;
            this.port = port;
            this.pos = pos;
        }

        /**
         * I/O operate
         *
         * @param conn Communication
         * @param request IRequest
         * @return IResponse
         */
        public IResponse operate(IRequest request) {
            //short cut calling
            if (JnetEnv.getJnetLocalFS(pos)) {
                try {
                    return Handle.getJnetFSImpl(request).operate(request);
                } catch (Exception ex) {
                    return ResponseImpl.getInstance(IResponse.EIO);
                }
            }
            // remote calling
            IResponse response = null;
            try {
                connect();
                //send out request
                JnetJNIConnector connector = request.getConnector();
                connector.write(outChanel);
                outChanel.flush();
                //receive the result
                connector.read(inChanel);
                response = ResponseImpl.getInstance(connector.getInteger("error.code"), connector);
            } catch (Exception ex) {
//	      ex.printStackTrace(); 
                broken = true;
                tryTime = System.currentTimeMillis() + JnetEnv.getJetReconnectTimeout(pos);
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (Exception ex1) {
                }
                socket = null;
                outChanel = null;
                inChanel = null;
                response = ResponseImpl.getInstance(IResponse.EACCES);
            }
            return response;
        }

        /**
         * create sockets
         *
         * @throws IOException IOException
         */
        private void connect() throws IOException {
            if (broken) {
                if (tryTime > System.currentTimeMillis()) {
                    throw new IOException();
                }
                broken = false;
            }
            if (socket != null) {
                return;
            }
            int port = Server.PORT;
            try {
                port = Integer.parseInt(this.port);
            } catch (NumberFormatException ex) {
            }
            socket = new Socket(server, port);
            socket.setTcpNoDelay(true);
            outChanel = new DataOutputStream(
                    new SecretOutputStream(new BufferedOutputStream(socket.getOutputStream())));
            inChanel = new DataInputStream(
                    new SecretInputStream(new BufferedInputStream(socket.getInputStream())));
            broken = true;
        }
    }
}
