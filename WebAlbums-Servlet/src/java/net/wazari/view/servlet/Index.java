package net.wazari.view.servlet;


import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.ThemeLocal;
import net.wazari.service.ThemeLocal.Sort;
import net.wazari.service.exchange.ViewSession.ViewSessionTheme;
import net.wazari.service.exchange.xml.XmlThemes;
import net.wazari.view.servlet.DispatcherBean.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebServlet(
    name = "Index",
    urlPatterns = {"/Index"}
)
@Stateless
public class Index extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(Index.class.getName()) ;
    
    @EJB DispatcherBean dispatcher ;
    @EJB ThemeLocal themeService ;
    
    public XmlThemes treatVOID(ViewSessionTheme vSession) {
        XmlThemes output = new XmlThemes() ;

        boolean statik = vSession.getVSession().getStatic();
        if (statik) {
            vSession.getSessionConfig().setStatic(true);
            vSession.getSessionConfig().setDirectFileAccess(true);
        } else {
            vSession.getSessionConfig().setDirectFileAccess(vSession.getVSession().directFileAccess());
        }

        output.themeList = themeService.getThemeList(vSession, Sort.ALBUM_AGE) ;
        
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
        dispatcher.treat(this.getServletContext(), Page.VOID, request, response);
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
        return "Displays the list of themes";
    }// </editor-fold>
}
