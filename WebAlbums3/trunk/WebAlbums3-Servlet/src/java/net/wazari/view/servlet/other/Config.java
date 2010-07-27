/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.exchange.Configuration;
import net.wazari.view.servlet.exchange.ConfigurationXML;
import net.wazari.common.util.XmlUtils;

/**
 *
 * @author kevinpouget
 */
@WebServlet(name = "ViewConfig",
urlPatterns = {"/Other/Config"})
public class Config extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(Config.class.getName());

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String action = request.getParameter("action");

            log.info( "Other/Config action:{}", action);
            if ("LOGOUT".equals(action)) {
                request.logout(); 
            } else if ("CREATE_DIRS".equals(action)) {
                Configuration conf = ConfigurationXML.getConf();

                out.println("Root path:"+conf.getRootPath()+"<BR/>") ;
                List<String> directories = Arrays.asList(
                        new String[]{
                            conf.getBackupPath(), conf.getTempPath(), conf.getPluginsPath(),
                            conf.getFtpPath(), conf.getImagesPath(), conf.getMiniPath()});
                for (String dir : directories) {
                    File currentFile = new File(dir) ;
                    if (!(currentFile.isDirectory() || currentFile.mkdirs())) {
                        log.warn( "Couldn't create {}", dir);
                        out.println("WARNING Couldn't create "+ dir+"<BR/>") ;
                    } else {
                        out.println(dir+"<BR/>") ;
                    }
                }
                File confFile = new File(conf.getConfigFilePath()).getParentFile() ;
                if (!(confFile.isDirectory() || confFile.mkdirs())) {
                    log.warn( "Couldn't create path to {}",
                            conf.getConfigFilePath());
                    out.println("WARNING Couldn't create path to "+ conf.getConfigFilePath()+"<BR/>") ;
                } else {
                    if (!confFile.exists()) {
                        File file = new File(ConfigurationXML.getConf().getConfigFilePath());
                        XmlUtils.save(file, ConfigurationXML.getConf(), ConfigurationXML.class);
                        out.println("INFO Config file saved in "+ conf.getConfigFilePath()+"<BR/>") ;
                   } else {
                        out.println("INFO Config file already exists in "+ conf.getConfigFilePath()+"<BR/>") ;
                   }
                }

            } else {
                if ("SAVE".equals(action)) {
                    File file = new File(ConfigurationXML.getConf().getConfigFilePath());
                    file.getParentFile().mkdirs();

                    XmlUtils.save(file, ConfigurationXML.getConf(), ConfigurationXML.class);

                    log.info( "ConfigurationXML Saved into {}", file.getCanonicalPath());
                } else if ("RELOAD".equals(action)) {

                    File file = new File(ConfigurationXML.getConf().getConfigFilePath());
                    file.getParentFile().mkdirs();

                    ConfigurationXML conf = XmlUtils.reload(new FileInputStream(file), ConfigurationXML.class);

                    ConfigurationXML.setConf(conf);
                    log.info( "ConfigurationXML Reloaded from {}", file.getCanonicalPath()) ;
                }
                response.setContentType("text/xml;charset=UTF-8");

                String xml = XmlUtils.print((ConfigurationXML) ConfigurationXML.getConf(),
                        ConfigurationXML.class);

                out.println(xml);

            }

        } catch (Exception e) {
            log.error(e.getClass().toString(), e);
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        return "Short description";
    }// </editor-fold>
}
