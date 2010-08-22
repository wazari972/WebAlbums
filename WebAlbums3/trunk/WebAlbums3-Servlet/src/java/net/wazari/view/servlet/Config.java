package net.wazari.view.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import net.wazari.common.util.XmlBuilder;
import net.wazari.service.ConfigLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.view.servlet.DispatcherBean.Page;

@WebServlet(
    name = "Config",
    urlPatterns = {"/Config.html"}
)
@Stateless
public class Config extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @EJB private DispatcherBean dispatcher ;
    
    @EJB
    private ConfigLocal configService;
    @EJB
    private WebPageLocal webPageService ;

    public XmlBuilder treatCONFIG(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {

        Special special = vSession.getSpecial();
        if (special != null) {
            return new XmlBuilder("updated");
        }
        return displayCONFIG(vSession);
    }

    private XmlBuilder displayCONFIG(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("config");

        Action action = vSession.getAction();
        if (vSession.isSessionManager()) {

            output.add("map");
            if (action == Action.IMPORT) {
                output.add(configService.treatIMPORT(vSession));
            }

            //ajout d'un nouveau tag
            if (Action.NEWTAG == action) {
                output.add(configService.treatNEWTAG(vSession));
            }

            //Renommage d'un tag tag
            if (Action.MODTAG == action) {
                output.add(configService.treatMODTAG(vSession));
            }

            //Changement de visibilité d'un tag
            if (Action.MODVIS == action) {
                output.add(configService.treatMODVIS(vSession));
            }

            //modification d'une geolocalisation
            if (Action.MODGEO == action) {
                output.add(configService.treatMODGEO(vSession));
            }

            //suppression d'un tag
            if (Action.DELTAG == action) {
                output.add(configService.treatDELTAG(vSession));
            }

            //suppression d'un tag
            if (Action.DELTHEME == action) {
                output.add(configService.treatDELTHEME(vSession));
            }
            output.add(webPageService.displayListLB(Mode.TAG_USED, vSession, null,
                    Box.MULTIPLE));
            output.add(webPageService.displayListLB(Mode.TAG_GEO, vSession, null,
                    Box.MULTIPLE));
            output.add(webPageService.displayListLB(Mode.TAG_NEVER, vSession, null,
                    Box.MULTIPLE));

        } else {
            output.addException("Vous n'avez pas crée ce theme ...");
        }

        return output.validate();
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
        dispatcher.treat(this.getServletContext(), Page.CONFIG, request, response);
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
    private static final Logger log = LoggerFactory.getLogger(Config.class.getName());
}
