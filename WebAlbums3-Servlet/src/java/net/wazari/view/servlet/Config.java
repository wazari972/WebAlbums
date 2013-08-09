package net.wazari.view.servlet;

import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.ConfigLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Tag_Mode;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exchange.ViewSessionConfig.Action;
import net.wazari.service.exchange.ViewSessionConfig.Special;
import net.wazari.service.exchange.xml.config.XmlConfig;
import net.wazari.view.servlet.DispatcherBean.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(
    name = "Config",
    urlPatterns = {"/Config"}
)
@Stateless
public class Config extends HttpServlet {

    private static final String SHUTDOWN_PORT_PPT = "SHUTDOWN_PORT" ;

    private static final long serialVersionUID = 1L;
    @EJB private DispatcherBean dispatcher ;
    
    @EJB
    private ConfigLocal configService;
    @EJB
    private WebPageLocal webPageService ;
    
    public XmlConfig treatCONFIG(ViewSessionConfig vSession)
            throws WebAlbumsServiceException {

        if (!vSession.getVSession().isSessionManager())
            return null;
        
        Special special = vSession.getSpecial();
        
        XmlConfig output = new XmlConfig();

        Action action = vSession.getAction();
        if (action == null) {
            action = Action.DEFAULT;
        }
        
        switch(action) {
            case IMPORT:
                output.irnport = configService.treatIMPORT(vSession);
                break;
            case NEWTAG:
                output.newtag = configService.treatNEWTAG(vSession);
                break;
            case MODTAG:
                output.modtag = configService.treatMODTAG(vSession);
                break;
            case SETHOME:
                output.sethome = configService.treatSETHOME(vSession);
                break;
            case MODVIS:
                output.modvis = configService.treatMODVIS(vSession);
                break;
            case MODGEO:
                output.modgeo = configService.treatMODGEO(vSession);
                break;
            case LINKTAG:
                output.linktag = configService.treatLINKTAG(vSession);
                break;
            case MODPERS:
                output.modpers = configService.treatMODPERS(vSession);
                break;
            case MODMINOR:
                output.modminor = configService.treatMODMINOR(vSession);
                break;
            case DELTAG:
                output.deltag = configService.treatDELTAG(vSession);
                break;
            case DELTHEME:
                output.deltheme = configService.treatDELTHEME(vSession);
                break;
            default:
                break;
        }

        if (System.getProperty(SHUTDOWN_PORT_PPT) != null) {
            output.shutdown = System.getProperty(SHUTDOWN_PORT_PPT);
        }
        
        if (special != Special.ONLY) {
            output.tag_used = webPageService.displayListLB(Tag_Mode.TAG_USED, vSession.getVSession(), null,
                    Box.MULTIPLE);
            output.tag_never = webPageService.displayListLB(Tag_Mode.TAG_NEVER, vSession.getVSession(), null,
                    Box.MULTIPLE);
            output.tag_geo = webPageService.displayListLB(Tag_Mode.TAG_GEO, vSession.getVSession(), null,
                    Box.MULTIPLE);
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
        dispatcher.treat(this.getServletContext(), Page.CONFIG, request, response);
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
        return "Configuration page";
    }// </editor-fold>
    private static final Logger log = LoggerFactory.getLogger(Config.class.getName());
}
