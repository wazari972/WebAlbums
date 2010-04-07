package net.wazari.view.servlet;

import javax.servlet.http.HttpServletRequest;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.view.servlet.Index.Page;

public class Tags extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        ViewSessionTag vSession = null;
        Index.treat(Page.TAGS, request, response, vSession);
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
