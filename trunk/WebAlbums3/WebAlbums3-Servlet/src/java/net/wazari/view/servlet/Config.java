package net.wazari.view.servlet;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import net.wazari.view.servlet.Index.Page;

@WebServlet(
    name = "Config",
    urlPatterns = {"/Config"}
)
public class Config extends HttpServlet {

    @EJB private Index idx ;
    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        idx.treat(this.getServletContext(), Page.CONFIG, request, response);
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
