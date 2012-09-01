/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.XmlPluginInfo;
import net.wazari.common.util.XmlUtils;
import net.wazari.service.DatabaseLocal;
import net.wazari.service.PluginManagerLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSessionDatabase;
import net.wazari.view.servlet.DispatcherBean.Page;
import net.wazari.view.servlet.exchange.ConfigurationXML;
import net.wazari.view.servlet.exchange.xml.XmlDatabase;
import net.wazari.view.servlet.exchange.xml.XmlDatabase.XmlCreateDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@WebServlet(
    name = "Database",
    urlPatterns = {"/Database"}
)
@Stateless
public class Database extends HttpServlet {
    @EJB private DispatcherBean dispatcher ;
    @EJB private DatabaseLocal databaseService;
    @EJB private WebPageLocal webPageService ;
    @EJB private PluginManagerLocal systemTools;
    public XmlDatabase treatDATABASE(ViewSessionDatabase vSession)
            throws WebAlbumsServiceException {
        XmlDatabase output = new XmlDatabase();

        Action action = vSession.getAction();
        if (action == null)
            action = Action.DEFAULT;
        
        if (vSession.isSessionManager()) {
            output.default_ = databaseService.treatDEFAULT(vSession);
//            output.default_.tag_used = webPageService.displayListLB(Mode.TAG_USED, vSession, null,
//                                                           Box.MULTIPLE);
            File file;
            switch (action) {
                case IMPORT:
                    output.import_ = databaseService.treatIMPORT(vSession);
                    break;
                case EXPORT:
                    output.export = databaseService.treatEXPORT(vSession);
                    break; 
                case TRUNK:
                    output.trunk = databaseService.treatTRUNK(vSession);
                    break;
                case CHECK_FS:
                case CHECK_DB:
                    output.check = databaseService.treatCHECK(vSession);
                    break;
                case STATS:
                    output.stats = databaseService.treatSTATS(vSession);
                    break;
                case UPDATE:
                    databaseService.treatUPDATE(vSession);
                    break;
                case UPDATE_DAO:
                    databaseService.treatUPDATE_DAO(vSession);
                    break;
                case RELOAD_PLUGINS:
                    systemTools.reloadPlugins(ConfigurationXML.getConf().getPluginsPath());
                case PLUGINS:
                    output.plugins = new XmlPluginInfo() ;
                    for (Importer imp : systemTools.getPluginList()) {
                        output.plugins.addImporter(imp);
                    }
                    output.plugins.setUsedSystem(systemTools.getUsedSystem()) ;
                    for (net.wazari.common.plugins.System sys :systemTools.getNotUsedSystemList()) {
                        output.plugins.addNotUsedSystem(sys);
                    }
                    break;
                case CREATE_DIRS:
                    output.create_dir = new XmlCreateDir(create_directories());
                    break;
                case SAVE_CONFIG:
                    file = new File(ConfigurationXML.getConf().getConfigFilePath());
                    file.getParentFile().mkdirs();
                    
                    try {
                        XmlUtils.save(file, ConfigurationXML.getConf(), ConfigurationXML.class);
                        output.message = "File saved in "+ConfigurationXML.getConf().getConfigFilePath();
                    } catch (Exception ex) {
                        output.exception = "Couldn't save the file: "+ex.getMessage();
                    }
                    break;
                case RELOAD_CONFIG:
                    file = new File(ConfigurationXML.getConf().getConfigFilePath());
                    file.getParentFile().mkdirs();

                    ConfigurationXML conf;
                    try {
                        conf = XmlUtils.reload(new FileInputStream(file), ConfigurationXML.class);
                        ConfigurationXML.setConf(conf);
                        output.message = "Configuration reloaded from "+ConfigurationXML.getConf().getConfigFilePath();
                    } catch (Exception ex) {
                        output.exception = "Couldn't reloaded the configuration file: "+ex.getMessage();
                    }

                    
                   break;
                case PRINT_CONFIG:
                    output.config = (ConfigurationXML) ConfigurationXML.getConf();
                    break;
            }
        } else {
            output.exception = "Vous n'êtes pas manager ..." ;
        }

        return output ;
    }
    
    private static List<String> create_directories() {
        Configuration conf = ConfigurationXML.getConf();
        List<String> out = new LinkedList<String>();
        out.add("Root path:"+conf.getRootPath()) ;
        
        List<String> directories = Arrays.asList(
                new String[]{
                    conf.getBackupPath(), conf.getTempPath(), conf.getPluginsPath(),
                    conf.getFtpPath(), conf.getImagesPath(true), conf.getMiniPath(true)});
        for (String dir : directories) {
            File currentFile = new File(dir) ;
            if (!(currentFile.isDirectory() || currentFile.mkdirs())) {
                log.warn( "Couldn't create {}", dir);
                out.add("WARNING Couldn't create "+ dir) ;
            } else {
                out.add(dir) ;
            }
        }
        File confFile = new File(conf.getConfigFilePath()).getParentFile() ;
        if (!(confFile.isDirectory() || confFile.mkdirs())) {
            log.warn( "Couldn't create path to {}",
                    conf.getConfigFilePath());
            out.add("WARNING Couldn't create path to "+ conf.getConfigFilePath()) ;
        } else {
            if (!confFile.exists()) {
                File file = new File(ConfigurationXML.getConf().getConfigFilePath());
                try {
                    XmlUtils.save(file, ConfigurationXML.getConf(), ConfigurationXML.class);
                    out.add("INFO Config file saved in "+ conf.getConfigFilePath()) ;
                } catch (JAXBException ex) {
                    out.add("WARNING couldn't save: "+ex.getMessage());
                }
           } else {
                out.add("INFO Config file already exists in "+ conf.getConfigFilePath()) ;
           }
        }
        return out;
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        dispatcher.treat(this.getServletContext(), Page.DATABASE, request, response);
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Configuration page";
    }// </editor-fold>
    private static final Logger log = LoggerFactory.getLogger(Database.class.getName());

}
