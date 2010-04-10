package display ;

import engine.* ;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;

import util.XmlBuilder;
import util.StringUtil ;

import constante.Path ;

public class Config extends HttpServlet {
  private static final long serialVersionUID = 1L;
	
  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    engine.Index.treat(WebPage.Page.CONFIG, request, response) ;
  }
  public void doPost(HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }
  

  public static XmlBuilder displayCONFIG(HttpServletRequest request)
				   throws HibernateException {
    XmlBuilder output = new XmlBuilder("config");
    
    String action = request.getParameter("action") ;    
    if (engine.Users.isLoggedAsCurrentManager(request)
	&& !Path.isReadOnly()) {

      output.add("map");
      if ("IMPORT".equals(action)) {
	output.add(engine.Config.treatIMPORT(request));
      }
            
      //ajout d'un nouveau tag
      if ("NEWTAG".equals(action)) {
       output.add(engine.Config.treatNEWTAG(request));	
      }
            
      //Renommage d'un tag tag
      if ("MODTAG".equals(action)) {
    	  output.add(engine.Config.treatMODTAG(request));	
      }

      //Changement de visibilité d'un tag
      if ("MODVIS".equals(action)) {
    	  output.add(engine.Config.treatMODVIS(request));	
      }
            
      //modification d'une geolocalisation
      if ("MODGEO".equals(action)) {
    	  output.add(engine.Config.treatMODGEO(request));	
      }
            
      //suppression d'un tag
      if ("DELTAG".equals(action)) {
    	  output.add(engine.Config.treatDELTAG(request));	
      }
      output.add(WebPage.displayListLB(WebPage.Mode.TAG_USED, request, null,
				       WebPage.Box.MULTIPLE));
      output.add(WebPage.displayListLB(WebPage.Mode.TAG_GEO, request, null,
				       WebPage.Box.MULTIPLE));
      output.add(WebPage.displayListLB(WebPage.Mode.TAG_NEVER, request, null,
				       WebPage.Box.MULTIPLE));

    } else {
      output.addException("Vous n'avez pas crée ce theme ...");
    }
    
    return output.validate() ;
  }
}