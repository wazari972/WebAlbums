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
import net.wazari.common.util.XmlBuilder;
import net.wazari.service.PhotoLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.view.servlet.DispatcherBean.Page;

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

    public XmlBuilder treatPHOTO(ViewSessionPhoto vSession) throws WebAlbumsServiceException {
        Action action = vSession.getAction();
        XmlBuilder output;
        XmlBuilder submit = null;
        Boolean correct = true;

        Special special = vSession.getSpecial();
        if (special == Special.RANDOM) {
            output = new XmlBuilder("photos");
            XmlBuilder random = new XmlBuilder("random");
            random.add(photoService.treatRANDOM(vSession)) ;
            return output.add(random) ;
        }

        if (Action.SUBMIT == action && vSession.isSessionManager()) {
            submit = photoService.treatPhotoSUBMIT((ViewSessionPhotoSubmit) vSession,correct);
        }
        
        if ((Action.EDIT == action || !correct) && vSession.isSessionManager()) {
            output = photoService.treatPhotoEDIT((ViewSessionPhotoEdit) vSession, submit);

            XmlBuilder return_to = new XmlBuilder("return_to");
            return_to.add("name", "Photos");
            return_to.add("count", vSession.getCount());
            return_to.add("album", vSession.getAlbum());
            return_to.add("albmCount", vSession.getAlbmCount());
            output.add(return_to);
        } else {
            output = new XmlBuilder("photos");
            output.add(photoService.treatPhotoDISPLAY((ViewSessionPhotoDisplay) vSession,submit));
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
