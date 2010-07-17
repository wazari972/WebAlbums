/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.bootstrap;
/*

 * To change this template, choose Tools | Templates

 * and open the template in the editor.

 */

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.api.ActionReport;
import org.glassfish.api.ActionReport.MessagePart;
import org.glassfish.api.admin.CommandRunner;
import org.glassfish.api.admin.ParameterMap;
import org.glassfish.api.embedded.ContainerBuilder;
import org.glassfish.api.embedded.EmbeddedDeployer;
import org.glassfish.api.embedded.LifecycleException;
import org.glassfish.api.embedded.Server;

/**
 *
 * @author pk033
 */
public class GF {
    private static final String PATH_RES = "/Users/kevinpouget/Assembla/WebAlbums3/trunk/WebAlbums3-DAO-JPABeans/setup/" ;
    private static final String PATH_EAR = "." ;
    private static final int PORT = 8081 ;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws LifecycleException, IOException, InterruptedException, Throwable {
        log.warning("Starting WebAlbums GF bootstrap") ;

        File ear =  new File(PATH_EAR, "WebAlbums3-ea.ear") ;
        if (!ear.isFile()) {
            log.log(Level.SEVERE, "Impossible to open the ear file at {0}", ear.getPath());
            return ;
        }

        File keyfile = new File("keyfile") ;
        if (keyfile.exists()) {
            log.warning("delete ./keyfile ") ;
            keyfile.delete();
        }

        try {
            new ServerSocket(PORT).close();
            new ServerSocket(PORT+1).close();
        } catch (BindException e) {
            log.log(Level.WARNING, "Port {0} or {1} already in use", new Object[]{PORT, PORT + 1}) ;
            return ;
        }

        Server server = startServer(8081) ;
        

        try {
            createUsers(server);
            createJDBC(server);

            EmbeddedDeployer deployer = server.getDeployer();
            log.info("Deploying ");
            String appName = null ;
            appName = deployer.deploy(new File(PATH_EAR, "WebAlbums3-ea.ear"), null);
            log.log(Level.INFO, "Deployed {0}", appName);
            
            log.log(Level.INFO, "Ready to server at http://localhost:{0}/WebAlbums3-Servlet", Integer.toString(PORT));
            log.log(Level.INFO, "Connect to http://localhost:{0} to shutdown the server", Integer.toString(PORT+1));

            ServerSocket serv = new ServerSocket(PORT+1) ;
            serv.accept().close() ;
            serv.close() ;
            
            server.stop();
        } finally {
            server.stop();
            if (keyfile.exists()) {
                log.warning("delete ./keyfile ") ;
                keyfile.delete();
            }
        }
    }

    private static Server startServer(int port) throws LifecycleException, IOException {
        Server.Builder builder = new Server.Builder("WebAlbums");
        Server server = builder.build();
        server.addContainer(ContainerBuilder.Type.all);
        server.start();

        // Specify the port
        server.createPort(port);

        return server ;

    }

    private static List<MessagePart> asAdmin(Server server, String command, ParameterMap params) throws Throwable {
        CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
        ActionReport report = server.getHabitat().getComponent(ActionReport.class);
        log.log(Level.INFO, "Invok {0} {1}", new Object[]{command, params});

        log.log(Level.INFO, "command \"{0}\" invoked", command);
        if (params == null) {
            runner.getCommandInvocation(command, report).execute();
        } else {
            runner.getCommandInvocation(command, report).parameters(params).execute();
        }
        log.log(Level.INFO, "command finished with {0}", report.getActionExitCode());
        
        if (report.hasFailures()){
            if (report.getFailureCause() != null) {
                throw report.getFailureCause();
            } else {
                throw new Exception (report.getMessage()) ;
            }
        }
        log.log(Level.INFO, "command finished with {0}", report.getActionExitCode());
        return report.getTopMessagePart().getChildren() ;
    }

    private static void createUsers(Server server) {
        ParameterMap params = new ParameterMap();
        params.add("groups", "Admin");
        params.add("groups", "Manager");
        params.add("username", "Kevin");
        params.add("userpassword", "pass");
        try {
            asAdmin(server, "create-file-user", params);
        } catch (Throwable t){
            //t.printStackTrace();
        }
    }

    private static void createJDBC(Server server) throws Throwable {
        ParameterMap params = new ParameterMap();
        params.add("datasourceclassname", "com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        params.add("restype", "javax.sql.DataSource");
        params.add("isisolationguaranteed", "true");
        params.add("isconnectvalidatereq", "false");
        params.add("validationmethod", "auto-commit");
        params.add("failconnection", "false");
        params.add("allownoncomponentcallers", "false");
        params.add("nontransactionalconnections", "false");
        params.add("leakreclaim", "false");
        params.add("lazyconnectionenlistment", "false");
        params.add("lazyconnectionassociation", "false");
        params.add("associatewiththread", "false");
        params.add("matchconnections", "false");
        params.add("ping", "true");
        params.add("pooling", "true");

        params.add("wrapjdbcobjects", "false");

        params.add("property", "URL=jdbc\\:mysql\\://127.0.0.1/WebAlbums_test:User=wazari972:Password=ijaheb");
        params.add("DEFAULT", "cpMySQLWebAlbumsTest");
        asAdmin(server, "create-jdbc-connection-pool", params);


        for (MessagePart msg : asAdmin(server, "list-jdbc-connection-pools", null)) {
            log.log(Level.INFO, "Connection pool: {0}", msg.getMessage());
        }


        params = new ParameterMap();
        params.add("connectionpoolid", "cpMySQLWebAlbumsTest");
        params.add("enabled", "true");
        params.add("DEFAULT", "jdbc/mysqlWebAlbumsTest");
        asAdmin(server, "create-jdbc-resource", params);

        for (MessagePart msg : asAdmin(server, "list-jdbc-resources", null)) {
            log.log(Level.INFO, "JDBC connection : {0}", msg.getMessage());
        }
    }

    private static void createJDBC_add_Resournces(Server server) throws Throwable {
        ParameterMap params = new ParameterMap();
        params.add("", PATH_RES + "sun-resources.xml");
        asAdmin(server, "add-resources", params);
    }

    private static final Logger log = Logger.getLogger(GF.class.getName());
}