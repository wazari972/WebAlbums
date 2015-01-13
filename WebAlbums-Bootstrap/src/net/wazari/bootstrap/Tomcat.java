/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;

import java.io.File;
import java.io.IOException;
import org.apache.catalina.startup.Bootstrap;

/**
 *
 * @author kevin
 */
public class Tomcat implements AppServer {
    private Bootstrap boot;

    @Override
    public void start(int port) throws AppServerException, IOException {
        
        try {
            boot = new Bootstrap();
            boot.start();
        } catch (Exception ex) {
            throw new AppServerException(ex);
        }
    }
    
    @Override
    public void terminate() throws AppServerException {
        try {
            boot.stop();
        } catch (Exception ex) {
            throw new AppServerException(ex);
        }
    }

    @Override
    public void createUsers(Util.Config.User usr) {
        
    }

    @Override
    public void createJDBC_add_Resources(String sunResourcesXML) throws AppServerException {
        
    }

    @Override
    public void deploy(File earfile) throws AppServerException {
        
    }
}
