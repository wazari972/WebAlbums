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
import net.wazari.service.AlbumLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.service.exchange.xml.album.XmlAlbumSubmit;
import net.wazari.view.servlet.DispatcherBean.Page;
import net.wazari.view.servlet.exchange.xml.XmlAlbums;

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

    public XmlAlbums treatALBM(ViewSessionAlbum vSession)
            throws WebAlbumsServiceException {

        XmlAlbums output = new XmlAlbums() ;

        Special special = vSession.getSpecial();
        if (special == Special.TOP5) {
            output.top = albumService.treatTOP(vSession);
            return output ;
        } else  if (special == Special.YEARS) {
            output.years = albumService.treatYEARS(vSession);
            return output ;
        } else  if (special == Special.SELECT) {
            output.select = albumService.treatSELECT(vSession);
            return output ;
        } else  if (special == Special.GRAPH) {
            output.graph = albumService.treatGRAPH(vSession);
            return output ;
        } else if (special == Special.ABOUT) {
            output.about = albumService.treatABOUT(vSession);
            return output ;
        } else if (special == Special.PHOTOALBUM_SIZE) {
            vSession.setPhotoAlbumSize(vSession.getPhotoAlbumSize());
            return null;
        }

        Action action = vSession.getAction();
        XmlAlbumSubmit submit = null;
        if(vSession.isSessionManager()) {
            //prepare SUBMIT message
            if (action == Action.SUBMIT) {
                submit = albumService.treatAlbmSUBMIT((ViewSessionAlbumSubmit) vSession);
            }

            if (action == Action.EDIT) {
                output.edit = albumService.treatAlbmEDIT((ViewSessionAlbumEdit) vSession, submit);
            }
        }

        if (action != Action.EDIT) {
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
