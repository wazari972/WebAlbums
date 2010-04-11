package net.wazari.view.servlet;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.view.servlet.Index.Page;

@WebServlet(
    name = "Albums",
    urlPatterns = {"/Albums"}
)
public class Albums extends HttpServlet{
    @EJB private Index idx ;
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        idx.treat(this.getServletContext(), Page.ALBUM, request, response);
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
