package net.wazari.view.servlet;


import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.view.servlet.DispatcherBean.Page;


@WebServlet(
    name = "Index",
    urlPatterns = {"/Index"}
)
public class Index extends HttpServlet {
    @EJB DispatcherBean dispatcher ;
    private static Logger log = Logger.getLogger(Index.class.toString()) ;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("WebPage initialized !");


        log.info("Ready to serve !");
    }
    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        dispatcher.treat(this.getServletContext(), Page.VOID, request, response);
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
