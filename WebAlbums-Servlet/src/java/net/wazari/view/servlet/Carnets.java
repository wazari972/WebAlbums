package net.wazari.view.servlet;

import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.CarnetLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionCarnet;
import net.wazari.service.exchange.ViewSessionCarnet.Carnet_Action;
import net.wazari.service.exchange.ViewSessionCarnet.Carnet_Special;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetSimple;
import net.wazari.service.exchange.xml.carnet.XmlCarnetSubmit;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.view.servlet.DispatcherBean.Page;
import net.wazari.view.servlet.exchange.xml.XmlCarnets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(
    name = "Carnets",
    urlPatterns = {"/Carnets"}
)
@Stateless
public class Carnets extends HttpServlet{
    @EJB private DispatcherBean dispatcher ;
    private static final long serialVersionUID = 1L;
    
    @EJB
    private CarnetLocal carnetService;
    @EJB 
    private WebPageLocal webPageService;
    
    public XmlCarnets treatCARNETS(ViewSessionCarnet vSession)
            throws WebAlbumsServiceException {
        XmlCarnets output = new XmlCarnets() ;
        
        Carnet_Special special = vSession.getCarnetSpecial();
        if (special == Carnet_Special.TOP5) {
            output.topCarnets = carnetService.treatTOP(vSession.getVSession());
            return output ;
        }
        
        Carnet_Action action = vSession.getCarnetAction();
        XmlCarnetSubmit submit = null;
        if(vSession.getVSession().isSessionManager()) {
            //prepare SUBMIT messag
            log.info("treat Carnet/{}", action);
            if (action == Carnet_Action.SUBMIT || action == Carnet_Action.SAVE) {
                submit = carnetService.treatSUBMIT(vSession.getSessionCarnetSubmit());
                if (action == Carnet_Action.SAVE) {
                    if (!submit.valid) {
                        output.exception = submit.exception ;
                    } else {
                        output.message = submit.carnet.getId().toString() ;
                    }
                    
                    return output;
                    
                } else if (submit != null && !submit.valid) {
                    action = Carnet_Action.EDIT;
                }
            }

            if (action == Carnet_Action.EDIT) {
                output.edit = carnetService.treatEDIT(vSession.getSessionCarnetEdit(), submit);
            }
        }

        if (action != Carnet_Action.EDIT) {
            //afficher la liste des albums de ce theme
            output.display = carnetService.treatDISPLAY(vSession.getSessionCarnetDisplay(), submit);
        }

        return output ;
    }
    
    public XmlWebAlbumsList treatJsonCARNET(ViewSessionCarnetSimple vSession) throws WebAlbumsServiceException {
        return webPageService.displayCarnetGeolocations(vSession);
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
        dispatcher.treat(this.getServletContext(), Page.CARNET, request, response);
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
        return "Displays the albums of the theme";
    }// </editor-fold>
    private static final Logger log = LoggerFactory.getLogger(Albums.class.getName());
}
