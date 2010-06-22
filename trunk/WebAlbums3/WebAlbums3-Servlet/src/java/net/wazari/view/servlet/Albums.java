package net.wazari.view.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.common.util.XmlBuilder;
import net.wazari.service.AlbumLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.view.servlet.DispatcherBean.Page;

@WebServlet(
    name = "Albums",
    urlPatterns = {"/Albums"}
)
public class Albums extends HttpServlet{
    @EJB private DispatcherBean dispatcher ;
    private static final long serialVersionUID = 1L;

    @EJB
    private AlbumLocal albumService;

    public XmlBuilder treatALBM(ViewSessionAlbum vSession)
            throws WebAlbumsServiceException {

        XmlBuilder output = new XmlBuilder("albums");
        
        Special special = vSession.getSpecial();
        if (special == Special.TOP5) {
            return albumService.treatTOP(vSession);
        }

        Action action = vSession.getAction();
        XmlBuilder submit = null;
        if(vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            //prepare SUBMIT message
            if (action == Action.SUBMIT) {
                submit = albumService.treatAlbmSUBMIT((ViewSessionAlbumSubmit) vSession);
            }

            if (action == Action.EDIT) {
                output = albumService.treatAlbmEDIT((ViewSessionAlbumEdit) vSession, submit);
            }
        }

        if (action != Action.EDIT) {
            //afficher la liste des albums de ce theme
            output.add(albumService.treatAlbmDISPLAY((ViewSessionAlbumDisplay)vSession, submit));
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
}
