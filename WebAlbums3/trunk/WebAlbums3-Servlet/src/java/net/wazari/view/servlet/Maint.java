/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.MaintLocal;
import net.wazari.service.exchange.ViewSessionMaint;
import net.wazari.service.exchange.xml.XmlMaint;
import net.wazari.view.servlet.DispatcherBean.Page;

/**
 *
 * @author kevinpouget
 */
@WebServlet(
    name = "Maint",
    urlPatterns = {"/Maint"}
)
@Stateless
public class Maint extends HttpServlet {
   
    private static final Logger log = LoggerFactory.getLogger(Maint.class.getCanonicalName()) ;
    private static final long serialVersionUID = 1L;
    @EJB private DispatcherBean dispatcher ;
    @EJB private MaintLocal maintService ;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("dispatch") ;
        dispatcher.treat(this.getServletContext(), Page.MAINT, request, response);
    }

    public XmlMaint treatMaint(ViewSessionMaint vSession) {
        try {
            log.info("treat maint") ;
            return maintService.treatMAINT(vSession) ;
        } catch (Exception e) {
            log.warn( "An exception occured in treatMaint:{}", e.getMessage());
            return null ;
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
        log.info("do get") ;
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
        log.info("do post") ;
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
