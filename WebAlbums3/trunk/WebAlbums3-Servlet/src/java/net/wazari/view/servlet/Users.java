package net.wazari.view.servlet;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import net.wazari.common.util.XmlBuilder;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSessionLogin;
import net.wazari.view.servlet.DispatcherBean.Page;

@WebServlet(
    name = "Users",
    urlPatterns = {"/Users"}
)
@Stateless
public class Users extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(Users.class.getCanonicalName()) ;
    private static final long serialVersionUID = 1L;
    @EJB private DispatcherBean dispatcher ;

    public XmlBuilder treatLogin(ViewSessionLogin vSession, HttpServletRequest request, HttpServletResponse response) throws IOException {
        XmlBuilder output = new XmlBuilder("userLogin");
        try {
            Action action = vSession.getAction();
            log.info( "Action: {}", action);
            if (Action.LOGIN == action) {

                String userName = vSession.getUserName();
                log.info( "userName: {}", userName);
                if (userName == null) {
                    output.add("denied");
                    output.add("login");
                    return output;
                }

                String pass = vSession.getUserPass();

                request.login(userName, pass);
                output.add("valid");
                log.info( "authentication");
                response.sendRedirect("Index");
                return null ;
            } else {
                output.add("login");
            }

        } catch (javax.servlet.ServletException e) {
            output.add("denied");
            output.add("login");

        } finally {
            output.validate() ;
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
        dispatcher.treat(this.getServletContext(), Page.USER, request, response);
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
        return "Login page";
    }// </editor-fold>
}
