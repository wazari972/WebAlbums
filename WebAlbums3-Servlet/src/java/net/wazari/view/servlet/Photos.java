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
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.Action;
import net.wazari.service.exchange.ViewSessionPhoto.Special;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionAnAlbum;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
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

    @EJB private PhotoLocal photoService;
    @EJB private WebPageLocal webPageService;

    public XmlPhotos treatPHOTO(ViewSessionPhoto vSession) throws WebAlbumsServiceException {
        Action action = vSession.getPhotoAction();
        XmlPhotos output = new XmlPhotos();
        XmlPhotoSubmit submit = null;
        Boolean correct = true;

        Special special = vSession.getPhotoSpecial();
        if (special == Special.RANDOM) {
            output.random = photoService.treatRANDOM(vSession.getVSession()) ;
            return output ;
        } else if (special == Special.ABOUT) {
//TEMP             output.about = photoService.treatABOUT(vSession.getVSession()) ;
            return output ;
        } else if (special == Special.FASTEDIT) {
            output.fastedit = photoService.treatFASTEDIT(vSession.getSessionPhotoFastEdit()) ;
            return output ;
        }

        if (Action.SUBMIT == action && vSession.getVSession().isSessionManager()) {
            submit = photoService.treatPhotoSUBMIT(vSession.getSessionPhotoSubmit(), correct);
        }
        
        if ((Action.EDIT == action || !correct) && vSession.getVSession().isSessionManager()) {
            ViewSessionPhotoEdit vSessionEdit = vSession.getSessionPhotoEdit();
            
            output.edit = photoService.treatPhotoEDIT(vSessionEdit, submit);

            XmlReturnTo return_to = new XmlReturnTo();
            return_to.name = "Photos" ;
            return_to.page = vSessionEdit.getPage();
            return_to.album = output.edit.details.albumId;
            
            return_to.albmPage = vSessionEdit.getAlbmPage();
            output.return_to = return_to;
        } else {
            output.display = photoService.treatPhotoDISPLAY(vSession.getSessionPhotoDisplay(),submit);
        }
        
        return output;
    }
    
    public XmlWebAlbumsList treatJsonPHOTO(ViewSessionAnAlbum vSession) throws WebAlbumsServiceException {
        return webPageService.displayAlbumGeolocations(vSession);
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
