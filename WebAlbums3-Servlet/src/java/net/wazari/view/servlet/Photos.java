package net.wazari.view.servlet;

import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.PhotoLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.xml.photo.XmlPhotoSubmit;
import net.wazari.view.servlet.DispatcherBean.Page;
import net.wazari.view.servlet.exchange.xml.XmlPhotos;
import net.wazari.view.servlet.exchange.xml.XmlReturnTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(
    name = "Photos",
    urlPatterns = {"/Photos"}
)
@Stateless
public class Photos extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB private DispatcherBean dispatcher ;

    @EJB
    private PhotoLocal photoService;

    public XmlPhotos treatPHOTO(ViewSessionPhoto vSession) throws WebAlbumsServiceException {
        Action action = vSession.getAction();
        XmlPhotos output = new XmlPhotos();
        XmlPhotoSubmit submit = null;
        Boolean correct = true;

        Special special = vSession.getSpecial();
        if (special == Special.RANDOM) {
            output.random = photoService.treatRANDOM(vSession) ;
            return output ;
        } else if (special == Special.ABOUT) {
            output.about = photoService.treatABOUT(vSession) ;
            return output ;
        } else if (special == Special.FASTEDIT) {
            output.fastedit = photoService.treatFASTEDIT((ViewSessionPhotoFastEdit) vSession) ;
            return output ;
        }

        if (Action.SUBMIT == action && vSession.isSessionManager()) {
            submit = photoService.treatPhotoSUBMIT((ViewSessionPhotoSubmit) vSession,correct);
        }
        
        if ((Action.EDIT == action || !correct) && vSession.isSessionManager()) {
            output.edit = photoService.treatPhotoEDIT((ViewSessionPhotoEdit) vSession, submit);

            XmlReturnTo return_to = new XmlReturnTo();
            return_to.name = "Photos" ;
            return_to.page = vSession.getPage();
            if (vSession.getAlbum() != null) {
                return_to.album = vSession.getAlbum();
            } else {
                return_to.album = output.edit.album;
            }
            
            return_to.albmPage = vSession.getAlbmPage();
            output.return_to = return_to;
        } else {
            output.display = photoService.treatPhotoDISPLAY((ViewSessionPhotoDisplay) vSession,submit);
        }


        return output;
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
        dispatcher.treat(this.getServletContext(), Page.PHOTO, request, response);
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
        return "Displays the photos of an album";
    }// </editor-fold>
    private static final Logger log = LoggerFactory.getLogger(Photos.class.getName());
}
