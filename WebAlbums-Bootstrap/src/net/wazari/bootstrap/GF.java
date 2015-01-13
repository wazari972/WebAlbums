/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import net.wazari.bootstrap.Util.Config.User;
import static net.wazari.bootstrap.Util.addToJavaLibraryPath;
import static net.wazari.bootstrap.Util.cfg;
import org.apache.catalina.LifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pk033
 */
public class GF implements AppServer {
    private static final Logger log = LoggerFactory.getLogger(GF.class.getName());
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            GF glassfish = new GF();
            glassfish.start(Util.cfg.port);
            //GF.waitForPortStop(Util.cfg.port + 1);
            glassfish.terminate();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public GlassFish server = null;
    public Deployer deployer = null;
    public String appName = null;
    public File keyfile = new File("keyfile");

    @Override
    public void start(int port) throws AppServerException, IOException {
        try {
            if (keyfile.exists()) {
                log.warn("delete ./keyfile ");
                keyfile.delete();
            }
            
            File installDirGF = new File("./glassfish").getCanonicalFile();
            Builder efsb = new Builder(); //EmbeddedFileSystem
            efsb.autoDelete(false);
            efsb.installRoot(installDirGF, true);
            
            // EAR not recognized with domain ...
            
            File instanceGF = null ; //new File(Util.cfg.glassfishDIR+"/domains/domain1");
            efsb.instanceRoot(instanceGF) ;
            EmbeddedFileSystem efs = efsb.build();
            
            server = startServer(port, "./glassfish");
            
            
        } catch (LifecycleException ex) {
            java.util.logging.Logger.getLogger(GF.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GlassFishException ex) {
            java.util.logging.Logger.getLogger(GF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void terminate() throws AppServerException {
        
        try {
            if (deployer != null && appName != null) {
                deployer.undeploy(appName);
            }
            if (server != null) {
                server.stop();
            }

            if (keyfile.exists()) {
                log.warn("delete ./keyfile ");
                keyfile.delete();
            }
        } catch(GlassFishException e) {
            throw new AppServerException(e);
        }
    }

    private static GlassFish startServer(int port, String glassfishDIR) throws LifecycleException, IOException, GlassFishException {
        /**
         * Create and start GlassFish which listens at 8080 http port
         */
        GlassFishProperties gfProps = new GlassFishProperties();
        gfProps.setPort("http-listener", port); // refer JavaDocs for the details of this API.
        
        System.setProperty("java.security.auth.login.config",
                glassfishDIR + "/config/login.conf");
        GlassFish glassfish;
        glassfish = null;//GlassFishRuntime.bootstrap().newGlassFish(gfProps);

        glassfish.start();

        return glassfish;
    }

    private static void asAdmin(GlassFish server, String command, ParameterMap params) throws Throwable {
        CommandRunner runner = server.getCommandRunner();

        log.info("Invoke {} {}", new Object[]{command, params});

        log.info("command \"{}\" invoked", command);
        ArrayList<String> paramLst = new ArrayList<>();
        if (params != null) {
            for (String key : params.keySet()) {
                for (String value : params.get(key)) {
                    if (key.length() != 0) {
                        paramLst.add("--" + key);
                    }
                    paramLst.add(value);
                }
            }
        }
        String[] paramArray = paramLst.toArray(new String[0]);
        CommandResult result = runner.run(command, paramArray);
        log.info("command finished with {}", result.getExitStatus());

        if (result.getFailureCause() != null) {
            throw new AppServerException(result.getFailureCause());
        }

        //log.info("--> {}", result.getOutput());
    }

    public void createUsers(User usr) throws AppServerException, IOException {
        ParameterMap params = new ParameterMap();
        File tmp = File.createTempFile("embGF-", "-Webalbums");
        {
            tmp.createNewFile();
            try (Writer out = new OutputStreamWriter(new FileOutputStream(tmp))) {
                out.append("AS_ADMIN_USERPASSWORD=" + usr.password);
            }
        }
        params.add("passwordfile", tmp.getAbsolutePath());
        params.add("groups", usr.groups);
        params.add("", usr.name);

        try {
            asAdmin(server, "create-file-user", params);
        } catch (Throwable ex) {
            throw new AppServerException(ex);
        }
        tmp.delete();
    }

    @Override
    public void createJDBC_add_Resources(String sunResourcesXML) throws AppServerException {
        try {
            ParameterMap params = new ParameterMap();
            params.add("", sunResourcesXML);
            asAdmin(server, "add-resources", params);
        } catch (Throwable ex) {
            throw new AppServerException(ex);
        }
    }

    @Override
    public void deploy(File earfile) throws AppServerException {
        deployer = server.getDeployer();
           
        appName = deployer.deploy(new File(cfg.webAlbumsEAR));
        if (appName == null) {
            log.info("Couldn't deploy ...");
            throw new AppServerException("Couldn't deploy ...");
        }
    }

    private static class GlassFish {

        public GlassFish() {
        }

        private Deployer getDeployer() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void start() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void stop() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private CommandRunner getCommandRunner() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class GlassFishProperties {

        public GlassFishProperties() {
        }

        private void setPort(String httplistener, int port) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class ParameterMap {

        public ParameterMap() {
        }

        private void add(String string, String path) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private Iterable<String> keySet() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private Iterable<String> get(String key) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class Deployer {

        public Deployer() {
        }

        private String deploy(File file) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void undeploy(String appName) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class Builder {

        public Builder() {
        }

        private void autoDelete(boolean b) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void installRoot(File installDirGF, boolean b) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void instanceRoot(File instanceGF) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private EmbeddedFileSystem build() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class EmbeddedFileSystem {

        public EmbeddedFileSystem() {
        }
    }

    private static class CommandResult {

        public CommandResult() {
        }

        private Exception getFailureCause() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private Object getExitStatus() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class CommandRunner {

        public CommandRunner() {
        }

        private CommandResult run(String command, String[] paramArray) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private static class GlassFishException extends RuntimeException {

        public GlassFishException() {
        }
    }
}
