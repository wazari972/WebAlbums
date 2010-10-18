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
import net.wazari.service.PhotoLocal;
import net.wazari.service.TagLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.view.servlet.exchange.xml.XmlTag;
import net.wazari.service.exchange.xml.photo.XmlPhotoEdit;
import net.wazari.service.exchange.xml.photo.XmlPhotoSubmit;
import net.wazari.view.servlet.DispatcherBean.Page;
import net.wazari.view.servlet.exchange.xml.XmlReturnTo;

@WebServlet(
    name = "Tags",
    urlPatterns = {"/Tags"}
)
@Stateless
public class Tags extends HttpServlet {
    @EJB private DispatcherBean dispatcher ;
    private static final long serialVersionUID = 1L;
    @EJB
    private TagLocal tagService;
    @EJB
    private PhotoLocal photoService;

    public XmlTag treatTAGS(ViewSessionTag vSession) throws WebAlbumsServiceException {
        XmlTag output = new XmlTag();
        Special special = vSession.getSpecial();
        if (special != null) {
            if (Special.CLOUD == special) {
                output.cloud = tagService.treatTagCloud(vSession) ;
            }

            if (Special.PERSONS == special || Special.PLACES == special) {
                output.personsPlaces = tagService.treatTagPersonsPlaces(vSession) ;
            }
            return output ;
        }

        Action action = vSession.getAction();
        XmlPhotoSubmit submit = null;
        Boolean correct = true;

        if (vSession.isSessionManager()) {
            if (Action.SUBMIT == action) {
                submit = photoService.treatPhotoSUBMIT((ViewSessionPhotoSubmit)vSession, correct);
            }

            if ((Action.EDIT == action || !correct)) {
                Integer[] tags = vSession.getTagAsked();
                Integer page = vSession.getPage();

                output.edit = photoService.treatPhotoEDIT((ViewSessionPhotoEdit) vSession, submit);
                XmlReturnTo return_to = new XmlReturnTo();
                return_to.name = "Tags" ;
                return_to.page = page;
                return_to.tagsAsked = tags;

                output.returnTo = return_to ;

                return output ;
            }
        }
        output.display = tagService.treatTagDISPLAY(vSession, submit) ;
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
        dispatcher.treat(this.getServletContext(), Page.TAGS, request, response);
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
        return "Displays photos related to a tag";
    }// </editor-fold>
    private static final Logger log = LoggerFactory.getLogger(Tags.class.getName());
}
