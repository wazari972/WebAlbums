/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import net.wazari.bootstrap.Util.Config.User;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.deploy.ContextResource;
import org.apache.catalina.startup.Tomcat;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.ConfigurationFactory;
import org.apache.openejb.config.DeploymentLoader;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.assembler.classic.Assembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pk033
 */
public class Tomee implements AppServer {
    private static final Logger log = LoggerFactory.getLogger(Tomee.class.getName());
    
    public static final String DEFAULT_CONFIG_PATH = "conf/config.xml";
    private static final String SHUTDOWN_PORT_PPT = "SHUTDOWN_PORT";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Tomee tomee = new Tomee();
            tomee.start(Util.cfg.port);
            Gui.waitForPortStop();
            tomee.terminate();
        } catch (AppServerException | IOException e) {
            e.printStackTrace();
        }
    }

    
    public Tomcat tomcat = null;

    @Override
    public void start(int port) throws AppServerException, IOException {
        tomcat = new Tomcat();
        tomcat.setPort(port);
        
        File tmp = File.createTempFile("tomcat", "-Webalbums");
        tmp.delete(); tmp.mkdir();
        
        tomcat.setBaseDir(tmp.getAbsolutePath());
                
        tomcat.getHost().setAutoDeploy(true); 
        tomcat.getHost().setDeployOnStartup(true); 
        
        tomcat.enableNaming();      
    }
    @Override
    public void deploy(File earfile) throws AppServerException {
        try {
            DeploymentLoader loader = new DeploymentLoader();
            AppModule appModule = loader.load(new File("/home/kevin/WebAlbums/apache-tomee-webprofile/apps/WebAlbums3-ea.ear"));
            ConfigurationFactory configurationFactory = new ConfigurationFactory();
            
            try {
                AppInfo appInfo = configurationFactory.configureApplication(appModule);
                Assembler assembler = (Assembler) SystemInstance.get().getComponent(org.apache.openejb.spi.Assembler.class);
                assembler.createApplication(appInfo);
            } catch (NullPointerException | IOException | NamingException ex) {
                java.util.logging.Logger.getLogger(Tomee.class.getName()).log(Level.SEVERE, null, ex);
            }
            Context WA;
            try {
                WA = tomcat.addWebapp("/toto", earfile.getAbsolutePath());
            } catch (ServletException ex) {
                throw new AppServerException("Couldn't create the application", ex);
            }
            
            WA.backgroundProcess();
            
            
            try {
                tomcat.init();
                tomcat.start();
            } catch (LifecycleException ex) {
                throw new AppServerException("Couldn't start Tomcat", ex);
            }
        } catch (OpenEJBException ex) {
            java.util.logging.Logger.getLogger(Tomee.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void terminate() throws AppServerException {
        try {
            if (tomcat != null) {
                tomcat.stop();
            }
            
        } catch (LifecycleException ex) {
            throw new AppServerException(ex);
        }
    }

    @Override
    public void createUsers(User usr) {
        this.tomcat.addUser(usr.name, usr.password);
        this.tomcat.addRole(usr.name, usr.groups.replace(":", ","));
    }

    @Override
    public void createJDBC_add_Resources(String path) throws AppServerException {
        try {
            if (!new File(path).exists()) {
                throw new IllegalArgumentException(path + " doesn't exists");
            }
            if (true)return;
            Context ctx = tomcat.addWebapp("/ctx", "context");
            
            ContextResource res = new ContextResource();
            res.setName("jdbc/mysqlWebAlbums");
            res.setType("javax.sql.DataSource");
            res.setAuth("Container"); //"com.mysql.jdbc.Driver");
            
            res.setProperty("username", "wazari972");
            res.setProperty("password", "ijaheb");
            res.setProperty("driverClassName", "com.mysql.jdbc.Driver");
            
            
            res.setProperty("url", "jdbc:mysql://127.0.0.1/WebAlbums");
            ctx.getNamingResources().addResource(res);
        } catch (ServletException ex) {
            throw new AppServerException("Couldn't create JDBC connection", ex);
        }
    }

    
}
