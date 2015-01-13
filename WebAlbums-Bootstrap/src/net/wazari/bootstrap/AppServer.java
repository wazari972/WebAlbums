/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author kevin
 */
public interface AppServer {

    public void terminate() throws AppServerException;

    public void start(int port) throws AppServerException, IOException ;

    public void createUsers(Util.Config.User usr) throws AppServerException, IOException;

    public void createJDBC_add_Resources(String sunResourcesXML) throws AppServerException;
    
     void deploy(File earfile) throws AppServerException;
    
    static class AppServerException extends Exception {

        AppServerException(String msg, Throwable e) {
            super(msg, e);
        }
        AppServerException(Throwable e) {
            super(e);
        }
        AppServerException(String msg) {
            super(msg);
        }
    }
}
