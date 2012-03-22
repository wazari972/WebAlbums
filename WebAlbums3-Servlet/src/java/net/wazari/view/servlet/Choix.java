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
import net.wazari.service.AlbumLocal;
import net.wazari.service.TagLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exchange.xml.XmlChoix;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.view.servlet.DispatcherBean.Page;

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
    
    public XmlWebAlbumsList displayChxJSON(ViewSession vSession) throws WebAlbumsServiceException {
        Special special = vSession.getSpecial();
        if (special == Special.MAP)
            return webPageService.displayMapInScript(vSession);
        else
            return null;
    }

    public XmlChoix displayCHX(ViewSession vSession) throws WebAlbumsServiceException {
        XmlChoix choix = new XmlChoix();
        Special special = vSession.getSpecial();
        
        if (special == Special.JUST_THEME) {
        } else {
            choix.tag_used = webPageService.displayListBN(Mode.TAG_USED, vSession,
                    Box.MULTIPLE, "tagAsked");
        }
        
        if (vSession.getCompleteChoix()) {
            ViewSessionAlbum vSessionAlbum = (ViewSessionAlbum) vSession;
            ViewSessionTag vSessionTag = (ViewSessionTag) vSession;
            
            choix.topAlbums = albumService.treatTOP(vSessionAlbum);
            choix.years = albumService.treatYEARS(vSessionAlbum);
            choix.select = albumService.treatSELECT(vSessionAlbum);
            choix.graph = albumService.treatGRAPH(vSessionAlbum);
            
            choix.persons = tagService.treatTagPersons(vSessionTag) ;
            choix.places = tagService.treatTagPlaces(vSessionTag) ;
            choix.map = webPageService.displayMapInScript(vSession).blob;
            
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
