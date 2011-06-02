/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet;

import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.DatabaseLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSessionDatabase;
import net.wazari.view.servlet.DispatcherBean.Page;
import net.wazari.view.servlet.exchange.xml.XmlDatabase;
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
    @EJB DatabaseLocal databaseService;
    
    public XmlDatabase treatDATABASE(ViewSessionDatabase vSession)
            throws WebAlbumsServiceException {
        XmlDatabase output = new XmlDatabase();

        Action action = vSession.getAction();
        if (vSession.isSessionManager()) {
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
                case CHECK:
                    output.check = databaseService.treatCHECK(vSession);
                    break;
                default:
                    output.default_ = databaseService.treatDEFAULT(vSession);
            }
        } else {
            output.exception = "Vous n'êtes pas manager ..." ;
        }

        return output ;
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
