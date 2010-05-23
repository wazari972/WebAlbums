package net.wazari.view.servlet;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.util.XmlBuilder;
import net.wazari.view.servlet.DispatcherBean.Page;

@WebServlet(
    name = "Users",
    urlPatterns = {"/Users"}
)
public class Users extends HttpServlet {

    private static final long serialVersionUID = 1L;
    @EJB private DispatcherBean dispatcher ;

    private static Logger log = Logger.getLogger(Login.class.getCanonicalName());


    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        dispatcher.treat(this.getServletContext(), Page.USER, request, response);
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public static XmlBuilder treatLogin(ViewSession vSession, HttpServletRequest request) {
        XmlBuilder output = new XmlBuilder("userLogin");
        try {
            Action action = vSession.getAction();
            log.info("Action: " + action);
            boolean valid = false;
            if (Action.LOGIN == action) {

                String userName = vSession.getUserName();
                log.info("userName: " + userName);
                if (userName == null) {
                    output.add("denied");
                    output.add("login");
                    return output;
                }

                String pass = vSession.getUserPass();

                request.login(userName, pass);
                output.add("valid");
                log.info("authentication: " + valid);
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
}
