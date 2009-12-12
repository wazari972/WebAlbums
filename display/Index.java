package display;

import engine.* ;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletConfig ;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import constante.Path;

public class Index extends HttpServlet {
  private static final long serialVersionUID = 1L;
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    Path.init(getServletContext());
  }

  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    String action = request.getParameter("action") ;
    
    if (action != null) {
	
      engine.Index.treat(WebPage.Page.MAINT, request, response) ;
    } else {
      engine.Index.treat(WebPage.Page.VOID, request, response);
    }
  }
  public void doPost(HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }  
}