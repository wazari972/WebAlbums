package net.wazari.view.servlet;

import javax.servlet.http.HttpServletRequest;


import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import net.wazari.common.util.XmlBuilder;
import net.wazari.service.TagLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.view.servlet.DispatcherBean.Page;

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

    public XmlBuilder treatTAGS(ViewSessionTag vSession) throws WebAlbumsServiceException {
        XmlBuilder output = new XmlBuilder("tags");
        Special special = vSession.getSpecial();
        if (Special.CLOUD == special) {
            return output.add(tagService.treatTagCloud(vSession)).validate() ;
        }

        if (Special.PERSONS == special || Special.PLACES == special || Special.RSS == special) {
            return output.add(tagService.treatTagPersonsPlaces(vSession)).validate() ;
        }


        Action action = vSession.getAction();
        XmlBuilder submit = null;
        Boolean correct = true;

        if (vSession.isSessionManager() && !vSession.getConfiguration().isReadOnly()) {
            if (Action.SUBMIT == action) {
                submit = tagService.treatPhotoSUBMIT((ViewSessionPhotoSubmit)vSession, correct);
            }

            if ((Action.EDIT == action || !correct)) {
                return tagService.treatTagEDIT(vSession, submit) ;
            }
        }
        return output.add(tagService.treatTagDISPLAY(vSession, submit)) ;
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
}
