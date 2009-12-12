package display ;

import engine.* ;
import javax.servlet.http.HttpServletRequest;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;

import util.XmlBuilder;

public class Choix extends HttpServlet {
  private static final long serialVersionUID = 1L;
	
  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    engine.Index.treat(WebPage.Page.CHOIX, request, response) ;
  }
  public void doPost(HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }

  public static XmlBuilder displayChxScript(HttpServletRequest request) {
    XmlBuilder output = WebPage.displayMapInScript(request, "mapChoix",
						   null);
    return output ;
  }
  
  public static XmlBuilder displayCHX(HttpServletRequest request)				      
    throws HibernateException {
    XmlBuilder choix = new XmlBuilder("choix");

    XmlBuilder tagList ;
    tagList = WebPage.displayListBN(WebPage.Mode.TAG_USED, request,
				    WebPage.Box.MULTIPLE, "tagAsked");

    choix.add(tagList) ;
	
    XmlBuilder tagMap ;
    tagMap = WebPage.displayMapInBody(request, "mapChoix",
				      null);
    choix.add(tagMap) ;

    choix.validate() ;

    return choix ;
  }
}