package net.wazari.view.servlet;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import net.wazari.view.servlet.DispatcherBean.Page;

@WebServlet(
    name = "Images",
    urlPatterns = {"/Images"}
)
public class Images extends HttpServlet {

    private static final long serialVersionUID = 1L;
    @EJB private DispatcherBean dispatcher ;
    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        dispatcher.treat(this.getServletContext(), Page.IMAGE, request, response);
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
