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
import net.wazari.service.ThemeLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Edit_Action;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionPhotoAlbumSize;
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

        ViewSessionAlbum.Album_Special special = vSession.getAlbumSpecial();
        
        if (special != null) {
            switch (special) {
                case TOP5:
                    output.topAlbums = albumService.treatTOP(vSession.getVSession());
                    break;
                case YEARS:
                    output.years = albumService.treatYEARS(vSession.getYearSession());
                    break;
                case SELECT:
                    output.select = albumService.treatSELECT(vSession.getSelectSession());
                    break;
                case GRAPH:
                    output.graph = albumService.treatGRAPH(vSession.getSelectSession());
                    break;
                case ABOUT:
                    output.about = albumService.treatABOUT(vSession.getSimpleSession());
                    break;
                case GPX:
                    output.gpxes = albumService.treatGPX(vSession.getVSession());
                    break;
                case AGO:
                    output.times_ago = albumService.treatAGO(vSession.getAgoSession());
                    break;
                case PHOTOALBUM_SIZE:
                    ViewSessionPhotoAlbumSize pasSession = vSession.getPhotoAlbumSizeSession();
                    pasSession.setPhotoAlbumSize(pasSession.getNewPhotoAlbumSize());
                    return null;    
            }
            return output;
        }
        
        Edit_Action action = vSession.getEditAction();
        XmlAlbumSubmit submit = null;
        if(vSession.getVSession().isSessionManager()) {
            //prepare SUBMIT message
            if (action == Edit_Action.SUBMIT) {
                submit = albumService.treatAlbmSUBMIT(vSession.getSubmitSession());
            }

            if (action == Edit_Action.EDIT) {
                ViewSessionAlbumEdit editSession = vSession.getEditSession();
                output.edit = new XmlAlbumEdit();
                output.edit.album  = albumService.treatAlbmEDIT(editSession);
                
                if (submit != null) {
                    output.edit.submit = submit ;
                }
                
                output.edit.tag_used = webPageService.displayListLB(ViewSession.Tag_Mode.TAG_USED, vSession.getVSession(), null,
                        ViewSession.Box.MULTIPLE);
                output.edit.tag_nused = webPageService.displayListLB(ViewSession.Tag_Mode.TAG_NUSED, vSession.getVSession(), null,
                        ViewSession.Box.MULTIPLE);
                output.edit.tag_never = webPageService.displayListLB(ViewSession.Tag_Mode.TAG_NEVER, vSession.getVSession(), null,
                        ViewSession.Box.MULTIPLE);

                output.edit.themes = themeService.getThemeListSimple(vSession.getVSession());
                
                XmlReturnTo returnTo = new XmlReturnTo();
                returnTo.page = editSession.getPage();

                output.return_to = returnTo ;
            }
        }

        if (action != Edit_Action.EDIT) {
            //afficher la liste des albums de ce theme
            output.display = albumService.treatAlbmDISPLAY(vSession.getDisplaySession(), submit);
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
