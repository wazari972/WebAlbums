package net.wazari.view.servlet;

import java.io.IOException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.WebPageLocal;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSessionBenchmark;
import net.wazari.service.exchange.ViewSessionBenchmark.BenchAction;
import net.wazari.view.servlet.DispatcherBean.Page;
import net.wazari.view.servlet.exchange.xml.XmlBenchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(
    name = "Benchmark",
    urlPatterns = {"/Benchmark"}
)
@Stateless
public class Benchmark extends HttpServlet{
    @EJB private DispatcherBean dispatcher ;
    private static final long serialVersionUID = 1L;
    
    @EJB
    private WebPageLocal webPageService;

    public XmlBenchmark treatBENCHMARK(ViewSessionBenchmark vSession)
            throws WebAlbumsServiceException {
        XmlBenchmark output = new XmlBenchmark();
        
        BenchAction action = vSession.getBenchAction();
        
        if (action == BenchAction.TAGS) {
            Mode mode = vSession.getMode();
            
            output.tag_used = webPageService.displayListBN(mode, vSession, Box.LIST);   
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
        dispatcher.treat(this.getServletContext(), Page.BENCHMARK, request, response);
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
