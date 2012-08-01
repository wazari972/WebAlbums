/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.libvfs.vfs.Resolver;
import net.wazari.service.ThemeLocal;
import net.wazari.view.vfs.entity.Root;

/**
 *
 * @author kevin
 */
public class Launch extends HttpServlet {
    
    @EJB public ThemeLocal themeService ;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Launch</title>");            
            out.println("</head>");
            out.println("<body>");
            try {
                request.login("kevin", "");
                out.println("<h1> Logged in</h1>");
            } catch (ServletException e) {
                out.println("<h1> ServletException" + e + "</h1>");
            }
            out.println("<h1> Logged in " + System.getProperty("java.library.path") + "</h1>");
            
            try {
                Root root = new Root(this);
                net.wazari.libvfs.vfs.LibVFS.resolver = new Resolver(root);
                com.jnetfs.core.JnetFS.main(new String[]{"/home/kevin/vayrac/WebAlbums/WebAlbums3-FS/test"});
            } catch (Exception e) {
                 out.println("<h1> JNetFSException" + e + "</h1>");
            }
            out.println("<h1> Logged in " + request.getUserPrincipal().getName() + "</h1>");
            out.println("<h1>Servlet Launch at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
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
