/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.other;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.SystemToolsLocal;
import net.wazari.view.servlet.exchange.ConfigurationXML;
import net.wazari.view.servlet.exchange.ViewSessionImpl;
import net.wazari.common.plugins.Importer;

/**
 *
 * @author kevinpouget
 */
@WebServlet(
    name = "ViewPlugins",
    urlPatterns = {"/Other/Plugins"}
)
public class Plugins  extends HttpServlet{
    private static final Logger log = Logger.getLogger(Plugins.class.getName());

    @EJB
    private SystemToolsLocal systemTools;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        ViewSessionImpl vSession = new ViewSessionImpl(request, response, this.getServletContext()) ;
        try {
            log.log(Level.INFO, "action:{0}", action);
            if ("RELOAD_PLUGINS".equals(action)) {
                systemTools.reloadPlugins(ConfigurationXML.getConf().getPluginsPath());
            } else if ("LIST_IMPORT_PLUGINS".equals(action)) {
                for (Importer imp :systemTools.getPluginList()) {
                    log.info("*"+imp.getClass());
                }
            }
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
