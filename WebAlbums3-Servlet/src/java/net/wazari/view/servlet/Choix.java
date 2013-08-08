package net.wazari.view.servlet;

import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.AlbumLocal;
import net.wazari.service.CarnetLocal;
import net.wazari.service.TagLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Choix_Special;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.ViewSessionChoix;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumAgo;
import net.wazari.service.exchange.ViewSessionCarnet;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exchange.xml.XmlChoix;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.view.servlet.DispatcherBean.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "Choix",
urlPatterns = {"/Choix"})
@Stateless
public class Choix extends HttpServlet {

    private static final long serialVersionUID = 1L;
    @EJB
    private DispatcherBean dispatcher;
    @EJB
    private WebPageLocal webPageService;
    @EJB
    private AlbumLocal albumService;
    @EJB
    private TagLocal tagService;
    @EJB
    private CarnetLocal carnetService;
    
    public XmlWebAlbumsList displayChxJSON(ViewSessionChoix vSession) throws WebAlbumsServiceException {
        Choix_Special special = vSession.getSpecial();
        if (special == Choix_Special.MAP) {
            return webPageService.displayMapInScript(vSession.getVSession());
        } else {
            return null;
        }
    }

    public XmlChoix displayCHX(ViewSessionChoix vSession) throws WebAlbumsServiceException {
        XmlChoix choix = new XmlChoix();
        Choix_Special special = vSession.getSpecial();
        
        if (special == Choix_Special.JUST_THEME) {
        } else {
            choix.tag_used = webPageService.displayListBN(Mode.TAG_USED, vSession.getVSession(), Box.MULTIPLE);
        }
        
        if (vSession.getVSession().getCompleteChoix() || vSession.getVSession().getStatic()) {
            ViewSessionAlbum vSessionAlbum = (ViewSessionAlbum) vSession;
            ViewSessionTag vSessionTag = (ViewSessionTag) vSession;
            ViewSessionCarnet vSessionCarnet = (ViewSessionCarnet) vSession;
            
            choix.topCarnets = carnetService.treatTOP(vSessionCarnet);
            choix.topAlbums = albumService.treatTOP(vSessionAlbum);
            choix.years = albumService.treatYEARS(vSessionAlbum);
            choix.select = albumService.treatSELECT(vSessionAlbum);
            choix.graph = albumService.treatGRAPH(vSessionAlbum);
            choix.times_ago = albumService.treatAGO((ViewSessionAlbumAgo) vSessionAlbum);
            
            choix.cloud = tagService.treatTagCloud(vSessionTag) ;
            choix.persons = tagService.treatTagPersons(vSessionTag) ;
            choix.places = tagService.treatTagPlaces(vSessionTag) ;
            choix.map = webPageService.displayMapInScript(vSession.getVSession()).blob;
            
            choix.complete = true;
        }

        return choix;
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
        dispatcher.treat(this.getServletContext(), Page.CHOIX, request, response);
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
        return "Displays the choice page";
    }// </editor-fold>
    private static final Logger log = LoggerFactory.getLogger(Choix.class.getName());
}
