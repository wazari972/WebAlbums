/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.BindException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.bootstrap.GF.Config.User;
import org.glassfish.api.admin.ParameterMap;
import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.internal.embedded.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pk033
 */
public class GF {
    private static final String SHUTDOWN_PORT_PPT = "SHUTDOWN_PORT" ;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws LifecycleException, IOException, InterruptedException, Throwable {
        long timeStart = System.currentTimeMillis() ;
        log.warn("Starting WebAlbums GF bootstrap");
    
/*        try {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            // the context was probably already configured by default configuration rules
            lc.reset(); 
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("logback.xml") ;
          
            configurator.doConfigure(stream);
            
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        } catch (Exception je) {
           je.printStackTrace();
           throw je;
        }
*/
        final Config cfg = Config.load();
        Integer stopPort = cfg.port + 1;
        log.info(Config.print(cfg)) ;
        System.setProperty(SHUTDOWN_PORT_PPT, stopPort.toString()) ;

        File keyfile = new File("keyfile");
        if (keyfile.exists()) {
            log.warn("delete ./keyfile ");
            keyfile.delete();
        }

        File earfile = new File(cfg.webAlbumsEAR);
        if (!earfile.exists()) {
            log.warn( "The earFile {} doesn't exist ...", earfile.getAbsolutePath());
            return;
        }
        log.warn( "Using EAR: {}", earfile);

        try {
            new ServerSocket(cfg.port).close();
            new ServerSocket(stopPort).close();
        } catch (BindException e) {
            log.warn( "Port {} or {} already in use", new Object[]{cfg.port, stopPort});
            return;
        }

        GlassFish server = null;
        Deployer deployer = null;
        String appName = null;
        try {
            log.warn( "Using Glassfish FS: {}", cfg.glassfishDIR);
            File installDirGF = new File(cfg.glassfishDIR).getCanonicalFile();

            EmbeddedFileSystem.Builder efsb = new EmbeddedFileSystem.Builder();
            efsb.autoDelete(false);
            efsb.installRoot(installDirGF, true);

            /* EAR not recognized with domain ...*/
            //File instanceGF = new File(cfg.glassfishDIR+"/domains/domain1");
            //efsb.instanceRoot(instanceGF) ;
            EmbeddedFileSystem efs = efsb.build();

            server = startServer(cfg.port, cfg.glassfishDIR);

            for (User usr : cfg.user) {
                createUsers(server, usr);
            }
            
            createJDBC_add_Resources(server, cfg.sunResourcesXML);

            deployer = server.getDeployer();
            
            log.info("Deploying EAR: {}", cfg.webAlbumsEAR);
            
            appName = deployer.deploy(new File(cfg.webAlbumsEAR));
            if (appName == null) {
                log.info( "Couldn't deploy ...");
                return;
            }
            log.info( "Deployed {}", appName);

            long loadingTime = System.currentTimeMillis();
            float time = ((float) (loadingTime - timeStart) / 1000);

            Runnable fs = new Runnable() {

                public void run() {
                    try {
                        log.info("Opening WebAlbums-FS");
                        final URL myURL = new URL("http://localhost:"+cfg.port+"/WebAlbums3-FS/Launch");
                        URLConnection myURLConnection = myURL.openConnection();
                        myURLConnection.connect();
                        log.info("WebAlbums-FS finished");
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(GF.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } ;
            new Thread(fs).start();
            
            log.info("Ready to server at http://localhost:{}/WebAlbums3.5-dev after {}s", new Object[] {Integer.toString(cfg.port), time});
            log.info("Try http://localhost:{}/WebAlbums3-FS/Launch to launch the Filesystem", new Object[] {Integer.toString(cfg.port)});
            log.info("Connect to http://localhost:{} to shutdown the server", Integer.toString(stopPort));

            ServerSocket servSocker = new ServerSocket(stopPort);
            servSocker.accept().close();
            servSocker.close();

        } finally {
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
        }
    }

    private static GlassFish startServer(int port, String glassfishDIR) throws LifecycleException, IOException, GlassFishException {
        /** Create and start GlassFish which listens at 8080 http port */
        GlassFishProperties gfProps = new GlassFishProperties();
        gfProps.setPort("http-listener", port); // refer JavaDocs for the details of this API.
        System.setProperty("java.library.path","/home/kevin/WebAlbums/WebAlbums3-FS/JnetFS_C/lib");
        System.setProperty("java.security.auth.login.config",
            glassfishDIR+"/config/login.conf");
        GlassFish glassfish = GlassFishRuntime.bootstrap().newGlassFish(gfProps);
        
        glassfish.start();
        
        return glassfish;
    }

    private static void asAdmin(GlassFish server, String command, ParameterMap params) throws Throwable {
        org.glassfish.embeddable.CommandRunner runner = server.getCommandRunner();

        log.info("Invoke {} {}", new Object[]{command, params});

        log.info("command \"{}\" invoked", command);
        ArrayList<String> paramLst = new ArrayList<String>();
        if (params != null) {
            for (String key : params.keySet()) {
                for (String value: params.get(key)) {
                    if (key.length() != 0) {
                        paramLst.add("--"+key);
                    }
                    paramLst.add(value);
                }
            }
        }
        String[] paramArray = paramLst.toArray(new String[0]);
        CommandResult result = runner.run(command, paramArray);
        log.info( "command finished with {}", result.getExitStatus());

        if (result.getFailureCause() != null) {
            throw result.getFailureCause();
        }

        //log.info("--> {}", result.getOutput());
    }

    private static void createUsers(GlassFish server, User usr) throws Throwable {
        ParameterMap params = new ParameterMap();
        File tmp = File.createTempFile("embGF-", "-Webalbums");
        {
            tmp.createNewFile();
            Writer out = new OutputStreamWriter(new FileOutputStream(tmp));
            out.append("AS_ADMIN_USERPASSWORD="+usr.password);
            out.close();
        }
        params.add("passwordfile", tmp.getAbsolutePath());
        params.add("groups", usr.groups);
        params.add("", usr.name);
        
        asAdmin(server, "create-file-user", params);
        tmp.delete();
    }

    private static void createJDBC_add_Resources(GlassFish server, String path) throws Throwable {
        if (!new File(path).exists()) {
            throw new IllegalArgumentException(path + " doesn't exists") ;
        }
        
        ParameterMap params = new ParameterMap();
        params.add("", path );
        asAdmin(server, "add-resources", params);
    }
    private static final Logger log = LoggerFactory.getLogger(GF.class.getName());

    @XmlRootElement

    static class Config {

        public static Config loadDefault(File cfgFile) throws JAXBException {
            Config cfg = new Config();
            cfg.sunResourcesXML = "conf/sun-resources.xml" ;
            cfg.glassfishDIR = "./glassfish" ;
            cfg.webAlbumsEAR = "bin/WebAlbums3-ea.ear" ;
            cfg.port = 8080 ;
            cfg.user = new LinkedList<User>() ;

            User usr = new User() ;
            usr.name = "Kevin" ;
            usr.password = "" ;
            usr.groups = "Admin:Manager" ;
            cfg.user.add(usr) ;

            if (!cfgFile.exists()) {
                save(cfgFile, cfg);
            }
            
            return cfg ;
        }

        public static Config load() throws JAXBException {
            File file = new File("conf/config.xml");
            if (!file.isFile()) {
                return loadDefault(file) ;
            }

            //Create JAXB Context
            JAXBContext jc = JAXBContext.newInstance(Config.class);
            Unmarshaller um = jc.createUnmarshaller();
            Config cfg = (Config) um.unmarshal(file);

            return cfg;
        }

        public static <T> String print(T xml) throws JAXBException {
            //Create JAXB Context
            JAXBContext jc = JAXBContext.newInstance(Config.class);

            //Create marshaller
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(xml, writer);

            return writer.toString();
        }

        public static <T> void save(File file, T obj) throws JAXBException {
            //Create JAXB Context
            JAXBContext jc = JAXBContext.newInstance(Config.class);
            //Create marshaller
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            file.getParentFile().mkdirs();
            marshaller.marshal(obj, file);
        }
        @XmlAttribute
        int port ;
        @XmlElement
        String sunResourcesXML ;
        @XmlElement
        String webAlbumsEAR ;
        @XmlElement
        String glassfishDIR ;
        @XmlElementWrapper(name="users")
        List<User> user ;

        static class User {
            @XmlAttribute String name ;
            @XmlAttribute String password ;
            @XmlAttribute String groups ;
        }
    }
}
