/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import com.jnetfs.core.JnetFS;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.libvfs.inteface.IResolver;
import net.wazari.libvfs.vfs.LibVFS;
import net.wazari.libvfs.vfs.Resolver;
import net.wazari.service.AlbumLocal;
import net.wazari.service.CarnetLocal;
import net.wazari.service.ImageLocal;
import net.wazari.service.PhotoLocal;
import net.wazari.service.TagLocal;
import net.wazari.service.ThemeLocal;
import net.wazari.service.WebPageLocal;
import net.wazari.view.vfs.entity.PhotoResolver;
import net.wazari.view.vfs.entity.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@WebServlet(
    name = "Launch",
    urlPatterns = {"/Launch"},
    loadOnStartup = 1
)
@Stateless
public class Launch extends HttpServlet {
    private static final String USERNAME = "kevin";
    private static final String PASSWORD = "";
    
    private static final Logger log = LoggerFactory.getLogger(Launch.class.getCanonicalName()) ;
    
    @EJB public ImageLocal imageService;
    @EJB public PhotoLocal photoService;
    @EJB public AlbumLocal albumService;
    @EJB public ThemeLocal themeService ;
    @EJB public CarnetLocal carnetService ;
    @EJB public TagLocal tagService;
    @EJB public WebPageLocal webPageService ;
    
    public static String getFolderPrefix(boolean getCompleteImagePath) {
        String path = System.getProperty("root.path") ;
        if (path == null) {
            path = "/other/Web/" ;
        }
        if (getCompleteImagePath) {
            path += "data/images/";
        }
        
        return path;
    }
    
    private void printAResponse(HttpServletRequest request, HttpServletResponse response,
            String path, ServletException ex) 
        {
        try (PrintWriter out = response.getWriter()) {
            out.println("<html>");
            out.println("<head>");
            out.println("<title>WebAlbum FS -- mounter</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h3> Mounting into "+path+".</h3>");
            
            
            if (ex == null) {
                out.println("<h1> Logged in as "+request.getUserPrincipal()+"</h1>");
                out.println("<h3> Done, goodbye :)</h3>");
            } else {
                log.warn("Login failed ...", ex);
                out.println("<h1>login failed "+ex.getMessage()+"</h1>");
            }
            
            out.println("</body>");
            out.println("</html>");
            out.close();    
        } catch (Exception e) {
            log.warn("<h1> JNetFSException </h1>", e);
        }
    }
    
    private void connectLibVFS(boolean mount, String path) {
        if (mount) {
            Root root = new Root(this);
            IResolver externalResolver = new PhotoResolver(root);
            LibVFS.resolver = new Resolver(root, getFolderPrefix(true), 
                    externalResolver, true);

            JnetFS.do_mount(new String[]{path});
        } else {
            JnetFS.do_umount(path);
        }
    }
    
    protected void processRequest(final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        boolean mount = request.getParameter("umount") != null;
        String path = request.getParameter("path") != null ? 
                request.getParameter("path") : "./WebAlbums3-FS";
        
        ServletException ex = null;
        try {
            request.login(USERNAME, PASSWORD);
        } catch(ServletException e) {
            ex = e;
        }
        
        printAResponse(request, response, path, ex);
        connectLibVFS(mount, path);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
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
     * Handles the HTTP
     * <code>POST</code> method.
     *
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
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
