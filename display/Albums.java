package display;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constante.Path;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import traverse.FilesFinder;
import util.StringUtil;
import engine.WebPage.AccessorsException ;
import engine.WebPage.Mode;
import engine.WebPage.Type;
import entity.Album;
import entity.Theme;
import entity.Utilisateur;

import engine.* ;

public class Albums {
 
  public static void treatAlbmEDIT(HttpServletRequest request,
				      StringBuilder output)
    throws HibernateException {
    String albumID = request.getParameter("id") ;
    String page = request.getParameter("page") ;
    String count = request.getParameter("count") ;
    page = (page == null ? "0" : page) ;
    
    String rq = null ;
    try {
      rq = "from Album where id = '"+albumID.replace("'", "''")+"'" ;
      List list = WebPage.session.find(rq);
      rq = "done" ;
      
      Album enrAlbum = (Album) list.iterator().next() ;
      
      StringBuilder str = new StringBuilder () ;
      
      str.append("<center>\n"+
		 "<b>Modification d'album</b><br/><br/>\n"+
		 ("0".equals(enrAlbum.getPicture()) ? "" :
		  "<img alt='"+StringUtil.escapeHTML(
		    enrAlbum.getNom())+
		  "' src='"+Path.LOCATION+".Images?id="+
		  enrAlbum.getPicture()+"&mode=PETIT' />\n")+
		 "</center>\n"+
		 "<form action='"+Path.LOCATION+".Albums?"+
		 "action=SUBMIT&count="+count+"&id="+enrAlbum.getID()+
		 "#"+enrAlbum.getID()+"' method='POST'>\n"+
		 "<table valign='middle'>\n"+
		 "	<tr>\n"+
		 "		<td align='right'>Nom :</td>" +
		 "		<td> <input type='text' size='100' "+
		 "maxlength='100' name='nom' value=\""+enrAlbum.getNom()+"\"/>"+
		 "</td>\n"+
		 "	</tr>\n" +
		 "	<tr>\n"+
		 "		<td align='right'>Description :</td>\n" +
		 "		<td> <textarea name='desc' "+
		 "rows='5' cols='100'>"+
		 enrAlbum.getDescription()+"</textarea></td>\n"+
		 "	</tr>\n" +
		 "	<tr>"+
		 "		<td align='right'>Date :</td><td> "+
		 "<input type='text' size='10' name='date' maxlength='10' "+
		 "value='"+WebPage.DATE_STANDARD.format(enrAlbum.getDate())+"'/>"+
		 "</td>\n"+
		 "	</tr>\n" +
		 "	<tr>\n"+
		 "		<td align='right'>Thèmes à appliquer "+
		 "aux photos:</td>\n" +
		 "		<td>\n" );
      WebPage.displayListLB(Mode.TAG_USED, request, str, null,
			    WebPage.Box.MULTIPLE);
      WebPage.displayListLB(Mode.TAG_NUSED, request, str, null,
			    WebPage.Box.MULTIPLE);
      WebPage.displayListLB(Mode.TAG_NONE, request, str, null,
			    WebPage.Box.MULTIPLE);
      
      str.append("		</td>\n"+
		 "	</tr>\n" +
		 "	<tr>\n"+
		 "		<td align='right'>Uniquement ?</td>\n" +
		 "		<td align='left'><input type='checkbox' "+
		 "name='force' value='yes' /></td>\n" +
		 "	</tr>\n"+
		 "	<tr>\n" +
		 "		<td align='right'> Utilisateurs :</td>\n"+
		 "		<td>\n");
      WebPage.displayListIBT(Mode.USER, request, str, enrAlbum.getID(),
			     WebPage.Box.MULTIPLE, Type.ALBUM ) ;
      str.append("		</td>\n"+
		 "	</tr>\n"+
		 "	<tr>\n"+
		 "		<td align='right'>Supprimer ?</td>\n" +
		 "		<td align='left'><input type='text' "+
		 "name='suppr' size='31' maxlength='31'/>" +
		 "		\"Oui je veux supprimer cet album\" "+
		 "(définitif !) </td>\n" +
		 "	</tr>\n"+
		 "	<tr>\n" +
		 "		<td align='center'><input type='submit' "+
		 "value='Valider' /></td>\n" +
		 "	</tr>\n"+
		 "<table>\n"+
		 "</form>\n"+
		 "<a href='"+Path.LOCATION+".Albums?count="+count+
		 "#"+enrAlbum.getID()+"'>Retour aux albums</a>\n");
      	    
      output.append(str.toString());
      
    } catch (JDBCException e) {
      output.append("<br/><i>Impossible de modifier l'album ("+albumID+") </i>"+
		    "=> "+rq+"<br/>\n"+e+e.getSQLException()+"<br/>\n");
    }
  }

  public static void displayAlbum(List list,
				  StringBuilder output,
				  HttpServletRequest request,
				  String message,
				  String pageGet)
    throws HibernateException {
    WebPage.EditMode inEditionMode = WebPage.getEditionMode(request) ;
    String albumID = request.getParameter("id") ;
    String page = request.getParameter("page") ;
    String countAlbm = StringUtil.escapeURL(request.getParameter("count")) ;
    page = (page == null ? "0" : page) ;
    
    Integer[] bornes =
      WebPage.calculBornes(Type.ALBUM, page, countAlbm, list.size()) ;
    List portionLst = list.subList(bornes[0], bornes[1]) ;
    
    int id ;
    try {
      id = Integer.parseInt(albumID) ;
      message = (message == null ? "" : message) ;
    } catch (NumberFormatException e) {
      id = -1 ;
      message = "" ;
    }
    
    Album enrAlbum;
    Iterator it = portionLst.iterator();
    String albmPict ;
    Date oldDate = null ;
    output.append("<table width='100%'>\n");
    int count = bornes[0] ;
    while (it.hasNext()) {
      enrAlbum = (Album) it.next();
      albmPict = (enrAlbum.getPicture() == null ? "" :
		  ("0".equals(enrAlbum.getPicture()) ? "" :
		   "<img alt='"+enrAlbum.getNom()+"' "+
		   "src='"+Path.LOCATION+".Images?"+
		   "id="+enrAlbum.getPicture()+"&mode=PETIT' />\n"));
      if (enrAlbum.getID() == id) {
	output.append("<tr><td colspan='4'>"+message+"</td><tr>\n") ;
      }
      output.append("	<tr align='center' valign='middle'>\n" +
		    "		<td>"+
		    StringUtil.displayDate(enrAlbum.getDate(), oldDate)+
		    "</td>\n" +
		    "		<td>\n"+
		    "			<a name='"+enrAlbum.getID()+"'></a>\n" +
		    albmPict+
		    "           </td>\n"+
		    "		<td><b>"+
		    "<a href='"+Path.LOCATION+".Photos?"+
		    "albmCount="+count+"&album="+enrAlbum.getID()+"'>"+
		    enrAlbum.getNom()+"</a></b></td>\n" +
		    "		<td width='70%' align='left'>\n" +
		    "		<table>\n" +
		    "			<tr>\n" +
		    "				<td>"+
		    enrAlbum.getDescription()+"</td>\n" +
		    "			</tr>\n" +
		    "			<tr>\n" +
		    "				<td>\n" +
		    "					<i>");
      WebPage.displayListIBT(Mode.TAG_USED, request, output, enrAlbum.getID(),
			     WebPage.Box.NONE, Type.ALBUM);
      output.append("</i>\n" +
		    "				</td>\n" +
		    "			</tr>\n");
      if (WebPage.isLoggedAsCurrentManager(request)
	  && !WebPage.isRootSession(request)) {
	if (inEditionMode != WebPage.EditMode.VISITE) {
	  output.append("			<tr>\n"+
			"				<td>\n");
	  WebPage.displayListIBT(Mode.USER, request, output, enrAlbum.getID(),
				 WebPage.Box.NONE, Type.ALBUM);
	  engine.Albums.displayListUserInside(request, output, enrAlbum.getID()) ;
	  output.append("\n				</td>\n");
	  output.append("			</tr>\n");
	}
      }		
      
      output.append("		</table>\n"+
		    "</td>\n"+
		    "<td>\n");
      if (WebPage.getMaps(request)) {
	WebPage.displayMapId(request, output, enrAlbum.getID());
      }
      output.append("</td>\n");
			
      if (WebPage.isLoggedAsCurrentManager(request)
	  && !WebPage.isRootSession(request)) {
	if (inEditionMode == WebPage.EditMode.EDITION) {
	  output.append("		<td width ='5%'>"+
			"                  <a href='"+
			""+Path.LOCATION+".Albums?"+
			"action=EDIT&id="+enrAlbum.getID()+
			"&count="+count+"'>\n"+
			"                     <img src='/data/web/edit.png' "+
			"width='25' heigh='25'>\n"+
			"                  </a>\n"+
			"               </td>\n");
	}
      }
			
      output.append("	</tr>\n");
			
      oldDate = enrAlbum.getDate();
      count++;
    }
    output.append("</table>\n");
    
    WebPage.displayPages(pageGet, bornes, output);
    
  }
}