/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.other;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.UserLocal;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.ViewSessionLogin;
import net.wazari.view.servlet.Images;
import net.wazari.view.servlet.exchange.ViewSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@WebServlet(name = "RandomPicture",
urlPatterns = {"/Other/Random"})
public class Random extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(Random.class.getName());

    @EJB
    private UserLocal userService;
    @EJB
    private Images imageServlet;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = null ;
        try {
            try {
                request.logout();
            } catch (ServletException e) {
                log.info("Wasn't logged in");
            }
            ViewSession vSession = null; //TEMP new ViewSessionImpl(request, response, getServletContext());
            request.login(request.getParameter("login"), "");
            boolean loggedin = userService.logon((ViewSessionLogin) vSession, request);
            log.debug( "Logon result: {}", loggedin);
            if (loggedin) {
                imageServlet.treatIMG((ViewSessionImages) vSession);
            } else {
                out = response.getWriter();
                out.write("<msg>couln't not log in ...</msg>");
            }

            userService.cleanUpSession((ViewSessionLogin) vSession);
            request.logout();
        } catch (Exception e) {
            log.warn("Exception during Random handling", e);
        } finally {
            if (out != null)
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
