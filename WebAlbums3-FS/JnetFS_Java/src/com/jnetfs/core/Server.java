/**
 * JNet-fs is based on the work Fuse-J. Copyright (C) 2009 Jacky WU
 * (hongzhi.wu@hotmail.com)
 *
 * This program can be distributed under the terms of the GNU LGPL. See the file
 * COPYING.LIB
 */
package com.jnetfs.core;

import java.net.ServerSocket;

import com.jnetfs.core.relay.Handle;
import com.jnetfs.core.relay.impl.JnetEnv;

/**
 * Server application
 *
 * @author jacky
 */
public class Server {

    /**
     * default work ports
     */
    public static final int PORT = 3332;

    /**
     * Program entrance
     *
     * @param args String[]
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception {
        startAppServer(args);
    }

    /**
     * start application server
     *
     * @param args String[]
     * @throws Exception Exception
     */
    public static void startAppServer(String[] args) throws Exception {
        int port = PORT;
        try {
            port = Integer.parseInt(JnetEnv.getServerPort());
        } catch (NumberFormatException ex) {
        }
        ServerSocket ssocket = new ServerSocket(port);
        String licence =
                "JavaNET FileSystem 1.0 Copyright (C) 2009 Jacky WU (hongzhi_wu@hotmail.com)\n"
                + "This program comes with ABSOLUTELY NO WARRANTY; for details type `show w'.\n"
                + "This is free software, and you are welcome to redistribute it\n"
                + "under certain conditions; type `show c' for details.\n"
                + "see <http://www.gnu.org/licenses/>";
        System.out.println(licence);
        while (true) {
            new Handle(ssocket.accept()).start();
        }
    }
}
