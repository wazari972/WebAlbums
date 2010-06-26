/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.other;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.view.servlet.exchange.ViewSessionImpl;

/**
 *
 * @author kevinpouget
 */
@WebServlet(name = "ViewDisplay",
urlPatterns = {"/Other/Display"})
public class Display extends HttpServlet {

    private static final Logger log = Logger.getLogger(Display.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        ViewSessionImpl vSession = new ViewSessionImpl(request, response, this.getServletContext()) ;
        try {
            log.log(Level.INFO, "action:{0}", action);
            if ("NEXT_EDITION".equals(action)) {
                 EditMode edit = vSession.getEditionMode() ;
                 edit.ordinal();
                 EditMode nextEdit = EditMode.values()[(edit.ordinal()+1) % EditMode.values().length] ;
                 log.log(Level.INFO, "Change EditMode from {0} to {1}", new Object[]{edit, nextEdit});
                 vSession.setEditionMode(nextEdit);
            } else if ("SWAP_DETAILS".equals(action)) {
                boolean details = vSession.getDetails() ;
                vSession.setDetails(!details);
                log.info("Change Details from "+details+" to "+!details);
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
