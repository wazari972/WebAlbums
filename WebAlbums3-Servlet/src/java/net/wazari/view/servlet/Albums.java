package net.wazari.view.servlet;

import java.awt.Desktop.Action;
import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.AlbumLocal;
import net.wazari.service.ThemeLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Album_Action;
import net.wazari.service.exchange.ViewSession.Album_Special;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.service.exchange.xml.album.XmlAlbumEdit;
import net.wazari.service.exchange.xml.album.XmlAlbumSubmit;
import net.wazari.view.servlet.DispatcherBean.Page;
import net.wazari.view.servlet.exchange.xml.XmlAlbums;
import net.wazari.view.servlet.exchange.xml.XmlReturnTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(
    name = "Albums",
    urlPatterns = {"/Albums"}
)
@Stateless
public class Albums extends HttpServlet{
    @EJB private DispatcherBean dispatcher ;
    private static final long serialVersionUID = 1L;
    @EJB
    private AlbumLocal albumService;
    @EJB
    private WebPageLocal webPageService;    
    @EJB
    private ThemeLocal themeService;
    
    public XmlAlbums treatALBM(ViewSessionAlbum vSession)
            throws WebAlbumsServiceException {

        XmlAlbums output = new XmlAlbums() ;

        Album_Special special = vSession.getSpecial();
        
        if (special != null) {
            switch (special) {
                case TOP5:
                    output.topAlbums = albumService.treatTOP(vSession);
                    break;
                case YEARS:
                    output.years = albumService.treatYEARS(vSession);
                    break;
                case SELECT:
                    output.select = albumService.treatSELECT(vSession);
                    break;
                case GRAPH:
                    output.graph = albumService.treatGRAPH(vSession);
                    break;
                case ABOUT:
                    output.about = albumService.treatABOUT(vSession);
                    break;
                case GPX:
                    output.gpxes = albumService.treatGPX(vSession);
                    break;
                case AGO:
                    output.times_ago = albumService.treatAGO((ViewSessionAlbum.ViewSessionAlbumAgo) vSession);
                    break;
                case PHOTOALBUM_SIZE:
                    vSession.setPhotoAlbumSize(vSession.getVSession().getPhotoAlbumSize());
                    return null;    
            }
            return output;
        }
        
        Album_Action action = vSession.getAction();
        XmlAlbumSubmit submit = null;
        if(vSession.getVSession().isSessionManager()) {
            //prepare SUBMIT message
            if (action == Album_Action.SUBMIT) {
                submit = albumService.treatAlbmSUBMIT((ViewSessionAlbumSubmit) vSession);
            }

            if (action == Album_Action.EDIT) {
                output.edit = new XmlAlbumEdit();
                output.edit.album  = albumService.treatAlbmEDIT((ViewSessionAlbumEdit) vSession);
                
                if (submit != null) {
                    output.edit.submit = submit ;
                }
                
                output.edit.tag_used = webPageService.displayListLB(ViewSession.Mode.TAG_USED, vSession.getVSession(), null,
                        ViewSession.Box.MULTIPLE);
                output.edit.tag_nused = webPageService.displayListLB(ViewSession.Mode.TAG_NUSED, vSession.getVSession(), null,
                        ViewSession.Box.MULTIPLE);
                output.edit.tag_never = webPageService.displayListLB(ViewSession.Mode.TAG_NEVER, vSession.getVSession(), null,
                        ViewSession.Box.MULTIPLE);

                output.edit.themes = themeService.getThemeList(vSession.getVSession(), ThemeLocal.Sort.NOPE);
                
                XmlReturnTo returnTo = new XmlReturnTo();
                returnTo.page = vSession.getPage();

                output.return_to = returnTo ;
            }
        }

        if (action != Album_Action.EDIT) {
            //afficher la liste des albums de ce theme
            output.display = albumService.treatAlbmDISPLAY((ViewSessionAlbumDisplay)vSession, submit);
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
        dispatcher.treat(this.getServletContext(), Page.ALBUM, request, response);
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
