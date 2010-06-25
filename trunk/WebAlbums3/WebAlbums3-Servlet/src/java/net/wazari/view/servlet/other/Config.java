/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.other;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import net.wazari.view.servlet.exchange.ConfigurationXML;
import net.wazari.view.servlet.utils.XmlUtils;

/**
 *
 * @author kevinpouget
 */
@WebServlet(
    name = "ViewConfig",
    urlPatterns = {"/Other/Config"}
)
public class Config extends HttpServlet {
    private static final Logger log = Logger.getLogger(Config.class.getName());

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

            
            log.info("action:"+action);
            if ("PRINT".equals(action)) {
                response.setContentType("text/xml;charset=UTF-8");
                
                String xml = XmlUtils.print((ConfigurationXML)ConfigurationXML.getConf(),
                        ConfigurationXML.class) ;
                
                out.println(xml) ;
            } else if ("SAVE".equals(action)) {
                File file = new File(ConfigurationXML.getConf().getConfigFilePath()) ;
                file.getParentFile().mkdirs();

                XmlUtils.save(file, ConfigurationXML.class) ;
                
                out.println("Saved into "+file.getCanonicalPath());
            } else if ("RELOAD".equals(action)) {
                File file = new File(ConfigurationXML.getConf().getConfigFilePath()) ;
                file.getParentFile().mkdirs();

                ConfigurationXML conf = XmlUtils.reload(file, ConfigurationXML.class) ;

                ConfigurationXML.setConf(conf);
                
                out.println("Reloaded "+file.getCanonicalPath());
            } else {

                out.println("nothing to do ...") ;
            }

        } catch (Exception e) {
            log.info("action:"+e.getMessage());
            e.printStackTrace();
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
