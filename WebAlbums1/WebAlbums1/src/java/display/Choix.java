package display ;

import engine.* ;
import javax.servlet.http.HttpServletRequest;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import constante.Path;

import org.hibernate.HibernateException;

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
  
  public static void displayCHX(HttpServletRequest request,
		       StringBuilder output)
    throws HibernateException {
    output.append("<b>Critères de choix : </b></br></br>\n"+
		  "<form action='"+Path.LOCATION+"Albums' "+
		  "method='get'>\n" +
		  "	Liste compléte des albums\n" +
		  "	<input type='submit' value='OK'/>\n" +
		  "</form></br></br>\n");
    
/*
  output.append("<form action='"+Path.LOCATION+"Periode' "+
  "method='get'>\n" +
  "	Par Année <input type='text' name='year' size='4' "+
  "maxlength='4'>\n" +
  "	[et mois <input type='text' name='month' size='2' "+
  "maxlength='2' />]\n" +
  "	<input type='submit' value='OK' />\n" +
  "</form></br></br>\n");
  
  output.append("<form action='"+Path.LOCATION+"Periode' "+
  "method='get'>\n" +
  "	Sur la Periode du <input type='text' name='debut' size='10' "+
  "maxlength='10' />\n" +
  "	au <input type='text' name='fin' size='10' maxlength='10' />\n" +
  "	<input type='submit' value='OK' /><br/>(format des dates : "+
  "JJ-MM-AAAA)\n" +
  "</form></br></br>\n");
*/
    output.append("<form action='"+Path.LOCATION+"Tags' "+
		  "mehod='get'>\n" +
		  "	<table>" +
		  "		<tr valign='middle'>\n" +
		  "			<td>\n" +
		  "				Par Thèmes :</td>\n" +
		  "			<td>\n");
    
    WebPage.displayListBN(WebPage.Mode.TAG_USED, request, output,
			  WebPage.Box.MULTIPLE, "tagAsked");
		
    output.append("			</td>\n" +
		  "			<td><input type='submit' value='OK'/>"+
		  "</td>\n" +
		  "		</tr>\n" +
		  "</table>\n" +
		  "</form>\n");
    //tag map
    WebPage.displayMapIn(request, output,
			 Path.LOCATION+"Tags?"+
			 "tagAsked=");

    /*
    //details
    output.append("<br/><br/>\n"+
		  "<form method='get' "+
		  "action='"+Path.LOCATION+"Choix'>\n" +
		  "Afficher les détails des photos ? "+
		  (WebPage.getDetails(request) ? "oui" : "non")+" \n" +
		  "<input type='hidden' name='details' value='"+
		  (WebPage.getDetails(request) ? "NON" : "OUI")+"'/>"+
		  "<input type='submit' value='Changer' />\n" +
				"</form>\n"+
		  "<br/><br/>\n");
    //albums map
    output.append("<form method='get' "+
		  "action='"+Path.LOCATION+"Choix'>\n" +
		  "Afficher les cartes des albums ? "+
		  (WebPage.getMaps(request) ? "oui" : "non")+" \n" +
		  "<input type='hidden' name='maps' value='"+
		  (WebPage.getMaps(request) ? "NON" : "OUI")+"'/>"+
		  "<input type='submit' value='Changer' />\n" +
		  "</form>\n"+
		  "<br/><br/>\n");
    */
    output.append("<br/><br/>\n");
    output.append("<a href='"+Path.LOCATION+"Users'>"+
		  "Retour aux utilisateurs</a>\n");
  }
}