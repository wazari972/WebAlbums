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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.api.ActionReport;
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
    private static final String PATH_EAR = "/Users/kevinpouget/Assembla/WebAlbums3/trunk/WebAlbums3-ea/dist/" ;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws LifecycleException, IOException, InterruptedException {
        Server server = startServer(80) ;

        createUsers(server);
        createJDBC(server);
        server.stop() ;
        if (true) return ;
        EmbeddedDeployer deployer = server.getDeployer();
        log.info("Deploying ");
        String appName = deployer.deploy(new File(PATH_EAR, "WebAlbums3-ea.ear"), null);
        log.log(Level.INFO, "Deployed {0}", appName);

    }

    private static Server startServer(int port) throws LifecycleException, IOException {
        Server.Builder builder = new Server.Builder("web-test");
        Server server = builder.build();
        server.addContainer(ContainerBuilder.Type.all);
        server.start();

        // Specify the port
        server.createPort(port);

        return server ;

    }

    private static void asAdmin(Server server, String command, ParameterMap params) {
        CommandRunner runner = server.getHabitat().getComponent(CommandRunner.class);
        ActionReport report = server.getHabitat().getComponent(ActionReport.class);
        log.log(Level.INFO, "Invok {0} {1}", new Object[]{command, params});
        runner.getCommandInvocation(command, report).parameters(params).execute();
        log.info("command invoked ");
    }

    private static void createUsers(Server server) {
        ParameterMap params = new ParameterMap();
        params.add("groups", "Admin, Manager");
        params.add("username", "Kevin");
        params.add("userpassword", "pass");
        asAdmin(server, "create-file-user", params);
    }

    private static void createJDBC(Server server) {
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
        params.add("ping", "false");
        params.add("pooling", "true");

        params.add("wrapjdbcobjects", "false");

        params.add("property", "URL=jdbc\\:mysql\\://127.0.0.1/WebAlbums_test:User=wazari972:Password=ijaheb");
        params.add("", "cpMySQLWebAlbumsTest");
        asAdmin(server, "create-jdbc-connection-pool", params);

        params = new ParameterMap();
        params.add("connectionpoolid", "cpMySQLWebAlbumsTest");
        params.add("enabled", "true");
        params.add("", "jdbc/mysqlWebAlbumsTest");
        asAdmin(server, "create-jdbc-resource", params);
    }

    private static void createJDBC_add_Resournces(Server server) {
        ParameterMap params = new ParameterMap();
        params.add("", PATH_RES + "sun-resources.xml");
        asAdmin(server, "add-resources", params);
    }

    private static final Logger log = Logger.getLogger(GF.class.getName());
}
