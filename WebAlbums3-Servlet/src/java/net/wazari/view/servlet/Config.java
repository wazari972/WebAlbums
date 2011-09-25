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
import net.wazari.service.ConfigLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exchange.xml.config.XmlConfig;
import net.wazari.view.servlet.DispatcherBean.Page;

@WebServlet(
    name = "Config",
    urlPatterns = {"/Config"}
)
@Stateless
public class Config extends HttpServlet {

    private static final String SHUTDOWN_PORT_PPT = "SHUTDOWN_PORT" ;

    private static final long serialVersionUID = 1L;
    @EJB private DispatcherBean dispatcher ;
    
    @EJB
    private ConfigLocal configService;
    @EJB
    private WebPageLocal webPageService ;
    
    public XmlConfig treatCONFIG(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {

        XmlConfig output = new XmlConfig();

        Action action = vSession.getAction();
        if (vSession.isSessionManager()) {

            if (action == Action.IMPORT) {
                output.irnport = configService.treatIMPORT(vSession);
            }

            //ajout d'un nouveau tag
            if (Action.NEWTAG == action) {
                output.newtag = configService.treatNEWTAG(vSession);
            }

            //Renommage d'un tag tag
            if (Action.MODTAG == action) {
                output.modtag = configService.treatMODTAG(vSession);
            }

            //Changement de visibilité d'un tag
            if (Action.MODVIS == action) {
                output.modvis = configService.treatMODVIS(vSession);
            }

            //modification d'une geolocalisation
            if (Action.MODGEO == action) {
                output.modgeo = configService.treatMODGEO(vSession);
            }

            //liens de parenté
            if (Action.LINKTAG == action) {
                output.linktag = configService.treatLINKTAG(vSession);
            }
            
            //details about a person
            if (Action.MODPERS == action) {
                output.modpers = configService.treatMODPERS(vSession);
            }
            
            //liens de parenté
            if (Action.LINKTAG == action) {
                output.linktag = configService.treatLINKTAG(vSession);
            }

            //suppression d'un tag
            if (Action.DELTAG == action) {
                output.deltag = configService.treatDELTAG(vSession);
            }

            //suppression d'un tag
            if (Action.DELTHEME == action) {
                output.deltheme = configService.treatDELTHEME(vSession);
            }

            if (System.getProperty(SHUTDOWN_PORT_PPT) != null) {
                output.shutdown = System.getProperty(SHUTDOWN_PORT_PPT) ;
            }
            
            output.tag_used = webPageService.displayListLB(Mode.TAG_USED, vSession, null,
                    Box.MULTIPLE);
            output.tag_never = webPageService.displayListLB(Mode.TAG_NEVER, vSession, null,
                    Box.MULTIPLE);

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
