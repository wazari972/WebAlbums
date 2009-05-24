package display ;

import engine.* ;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator ;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constante.Path;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import util.StringUtil;
import engine.WebPage.Mode;
import engine.WebPage.Type;

import entity.Tag ;
import entity.Geolocalisation ;

public class Config {

  public static void displayCONFIG(HttpServletRequest request,
				    StringBuilder output)
    throws HibernateException {
    String theme = request.getParameter("importTheme") ;
    String passwrd = request.getParameter("passwrd") ;
    String action = request.getParameter("action") ;
    StringBuilder strTags = new StringBuilder() ;
    StringBuilder strUsers = new StringBuilder() ;
    
    if (WebPage.isLoggedAsCurrentManager(request)
      && !WebPage.isRootSession(request)) {
      
      if ("IMPORT".equals(action)) {
	engine.Config.treatIMPORT(request, theme, passwrd, output);
      }
      output.append("<b>Importations des albums d'un auteur : </b><br/><br/>\n"+
		    "<form action='"+Path.LOCATION+".Config' "+
		    "method='get'>\n"+
		    "<input type='hidden' name='action' value='IMPORT'/>\n"+
		    "<input type='text' name='importTheme' size='20' "+
		    "maxlenght='20' "+
		    "value='"+WebPage.getThemeName(request)+"'/>\n"+
		    "<input type='password' name='passwrd' value=''/><br/>\n"+
		    "<input type='submit' value='Importer'/>\n"+
		    "</form>\n"+
		    "<br/>\n");
      
      //ajout d'un nouveau tag
      if ("NEWTAG".equals(action)) {
       engine.Config.treatNEWTAG(request, strTags);	
      }
      strTags.append("<b>Ajout d'un tag</b><br/><br/>\n"+
		     "<form action='"+Path.LOCATION+".Config' "+
		     "method='get'>\n"+
		     "<input type='hidden' name='action' value='NEWTAG'/>\n"+
		     "Nom : <input name='nom' type='text' size='20' "+
		     "maxlength='20'/> <br/>\n"+
		     "Type : "+
		     "<select name='type'>"+
		     "  <option value='-1'>========</option>\n"+
		     "  <option value='1' >[WHO]</option>\n"+
		     "  <option value='2' >[WHAT]</option>\n"+
		     "  <option value='3' >[WHERE]</option>\n"+
		     "</select>");
      
      strTags.append("<br/>Long/Lat : "+
		     "<input name='long' type='text' size='20' "+
		     "maxlength='20'/> <input name='lat' type='text' "+
		     "size='20' maxlength='20'/><br/>\n"+
		     "<input type='submit' value='Valider'/>\n"+
		     "</form>\n"+
		     "<br/>\n");
      
      //Renommage d'un tag tag
      if ("MODTAG".equals(action)) {
	engine.Config.treatMODTAG(request, strTags);	
      }
      strTags.append("<b>Renommage d'un tag</b><br/><br/>\n"+
		     "<form action='"+Path.LOCATION+".Config' "+
		     "method='get'>\n"+
		     "<input type='hidden' name='action' value='MODTAG'/>\n"+
		     "<table>" +
		     "	<tr>"+
		     "		<td align='left'> Ancien : </td>" +
		     "		<td>");
      WebPage.displayListB(Mode.TAG_ALL, request, strTags, WebPage.Box.LIST);
      strTags.append("		</td>"+
		     "	</tr>"+
		     "	<tr>"+
		     "		<td align='left'> Nouveau : </td>" +
		     "		<td>"+
		     "			<input name='nouveau' type='text' "+
		     "size='20' maxlength='20'/>\n"+
		     "		</td>"+
		     "	</tr>"+
		     "<table>" +
		     "<input type='submit' value='Valider'/>\n"+
		     "</form>\n"+
		     "<br/><br/>\n");

      //modification d'une geolocalisation
      if ("MODGEO".equals(action)) {
       engine.Config.treatMODGEO(request, strTags);	
      }
      strTags.append("<b>Modification d'une localisation</b><br/><br/>\n"+
		     "<form action='"+Path.LOCATION+".Config' "+
		     "method='get'>\n"+
		     "<input type='hidden' name='action' value='MODGEO'/>\n"+
		     "<table>" +
		     "	<tr>"+
		     "		<td align='left'> Tag : </td>" +
		     "		<td>");
      WebPage.displayListB(Mode.TAG_GEO, request, strTags, WebPage.Box.LIST);
      strTags.append("		</td>"+
		     "	</tr>"+
		     "	<tr>"+
		     "		<td align='left'> Long/Lat : </td>" +
		     "		<td>"+
		     "			<input name='lng' type='text' "+
		     "size='20' maxlength='20'/>\n"+
		     "                  <input name='lat' type='text' "+
		     "size='20' maxlength='20'/>\n"+
		     "		</td>"+
		     "	</tr>"+
		     "<table>" +
		     "<input type='submit' value='Valider'/>\n"+
		     "</form>\n"+
		     "<br/><br/>\n");
      
      //suppression d'un tag
      List<Integer> selected = null ;
      if ("DELTAG".equals(action)) {
	//if delete went wrong, selecte the one supposed to be removed
	selected = engine.Config.treatDELTAG(request, strTags);	
      }
      strTags.append("<b>Suppression d'un tag</b><br/><br/>\n"+
		     "<form action='"+Path.LOCATION+".Config' "+
		     "method='get'>\n"+
		     "<input type='hidden' name='action' value='DELTAG'/>\n");
      WebPage.displayListLB(Mode.TAG_ALL, request, strTags, selected,
			    WebPage.Box.LIST);
      strTags.append("<br/>\n" +
		     "Yes ? <input type='text' name='sure' size='3' "+
		     "maxlength='3'/><br/>\n"+
		     "<input type='submit' value='Valider' />\n"+
		     "</form>\n"+
		     "<br/><br/>\n");
      
      //ajout d'un utilisateur
      if ("NEWUSER".equals(action)) {
	engine.Config.treatNEWUSER(request, strUsers);	
      }
      strUsers.append("<b>Ajout d'un utilisateur</b><br/><br/>\n"+
		     "<form action='"+Path.LOCATION+".Config' "+
		     "method='get'>\n"+
		     "<input type='hidden' name='action' value='NEWUSER'/>\n"+
		     "Nom : <input name='nom' type='text' size='20' "+
		     "maxlength='20'/> <br/>\n"+
		     "Mot de passe : <input name='pass' type='text' "+
		     "size='20' maxlength='20'/><br/>\n"+
		     "<input type='submit' value='Valider' />\n"+
		     "</form>\n"+
		     "<br/><br/>\n");
      
      //suppression d'un utilisateur
      if ("DELUSER".equals(action)) {
	engine.Config.treatDELUSER(request, strTags);
      }
      
      strUsers.append("<b>Suppression d'un utilisateur</b><br/><br/>\n"+
		      "<form action='"+Path.LOCATION+".Config' "+
		      "method='get'>\n"+
		      "<input type='hidden' name='action' value='DELUSER'/>\n");
      WebPage.displayListB(Mode.USER, request, strUsers, WebPage.Box.LIST);
      strUsers.append("<br/>\n" +
		      "Yes ? <input type='text' name='sure' size='3' "+
		      "maxlength='3'/><br/>\n"+
		      "<input type='submit' value='Valider'/>\n"+
		      "</form>\n");

      output.append(strTags.toString());
      output.append(strUsers.toString());
      output.append("<br/><br/>\n");
      
    } else {
      output.append("<i> Vous n'avez pas crée ce theme ...</i><br/>\n");
    }
    
    output.append("<br/><br/><a href='"+Path.LOCATION+".Choix'>"+
		  "Retour aux choix</a><br/>\n");
  }
}