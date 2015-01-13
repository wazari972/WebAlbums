/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.jython;

import java.io.BufferedReader;
import org.python.core.PyException;
import org.python.core.PySystemState;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.wazari.service.AlbumLocal;
import net.wazari.service.ImageLocal;
import net.wazari.service.PhotoLocal;
import net.wazari.service.TagLocal;
import net.wazari.service.ThemeLocal;
import net.wazari.service.WebPageLocal;
import org.python.core.Py;
import org.python.core.PyDictionary;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import sun.org.mozilla.javascript.Interpreter;

/**
 *
 * @author kevin
 */
@WebServlet(
    name = "Launch",
    urlPatterns = {"/Launch"}
)
@Stateless
public class Launch extends HttpServlet {
    @EJB public ImageLocal imageService;
    @EJB public PhotoLocal photoService;
    @EJB public AlbumLocal albumService;
    @EJB public ThemeLocal themeService ;
    @EJB public TagLocal tagService;
    @EJB public WebPageLocal webPageService ;
       
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PySystemState state = new PySystemState();
        
        state.setClassLoader(Interpreter.class.getClassLoader());
        Map<PyObject,PyObject> map = new HashMap<>();
        map.put(new PyString("this"), Py.java2py(this));
        PyDictionary dict = new PyDictionary(map);
        
        BufferedReader terminal = new BufferedReader(new InputStreamReader(System.in));
        PythonInterpreter interp = new PythonInterpreter(dict);
       
        String codeString;
        String prompt = ">> ";
        while (true) {
          System.out.print (prompt);
          try {
            codeString = terminal.readLine();
            if (codeString == null || codeString.equals("exit")) {
              break;
            }
            if (!codeString.startsWith("print")) {
              codeString = "print "+codeString;
            }
            interp.exec(codeString);
          }
          catch (IOException e)
          {
            e.printStackTrace();
          } catch (final PyException pyException) {
            pyException.printStackTrace();
          }
        }
        System.out.println("Goodbye");
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
