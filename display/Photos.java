package display;


import engine.* ;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constante.Path;

import engine.WebPage.*;
import engine.WebPage ;
import entity.Album;
import entity.Photo;
import entity.Tag;
import entity.Theme;
import entity.Utilisateur ;


import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import traverse.FilesFinder;
import util.StringUtil;

public class Photos {

  public static void treatPhotoEDIT(HttpServletRequest request,
				       StringBuilder output)
    throws HibernateException {
    String photoID = StringUtil.escapeHTML(request.getParameter("id")) ;
    String page = StringUtil.escapeHTML(request.getParameter("page")) ;
    String count = StringUtil.escapeHTML(request.getParameter("count")) ;
    String albmCount = StringUtil.escapeURL(request.getParameter("albmCount")) ;
    page = (page == null ? "0" : page);
    String rq = null ;
    try {
      int theme = Integer.parseInt(WebPage.getThemeID(request)) ;
      
      rq = "from Photo where id = '"+photoID+"'" ;
      List list = WebPage.session.find(rq);
      rq = "done" ;
      
      Photo enrPhoto = (Photo) list.iterator().next() ;

      String from, name ;
      if (albmCount == null || albmCount == ""
	  || !"%".equals(albmCount.substring(0, 1))) {
	from = ""+Path.LOCATION+".Photos?"+
	  "count="+count+"&album="+enrPhoto.getAlbum()+"&albmCount="+albmCount ;
	name = "photos" ;
      } else {
	from = ""+Path.LOCATION+".Tags?"+
	  "count="+count+StringUtil.putbackAmp(albmCount) ;
	name = "tags" ;
      }
      StringBuilder str = new StringBuilder () ;
      
      str.append("<b>Modification de photo</b><br/><br/>\n"+
		 "<form action='"+from+"&action=SUBMIT&id="+photoID+
		 "#"+enrPhoto.getID()+"' method='POST'>\n"+
		 "<table border='0'>\n"+
		 "	<tr valign='middle'>\n"+
		 "		<td rowspan='3'><img alt='"+
		 enrPhoto.getDescription()+
		 "' src='"+Path.LOCATION+".Images?"+
		 "id="+enrPhoto.getID()+"&mode=PETIT' /></td>\n"+
		 "		<td align='right'>Description :</td>\n"+
		 "		<td colspan='2'><textarea type='text' "+
		 "name='desc' cols='50'>"+
		 enrPhoto.getDescription()+"</textarea></td>\n"+
		 "       </tr><tr>\n" +
		 "		<td align='right'>Thèmes :</td>\n"+
				   "		<td rowspan='2' valign='top'>\n");
      WebPage.displayListIBT(Mode.TAG_USED, request, str, enrPhoto.getID(),
			     WebPage.Box.MULTIPLE, Type.PHOTO) ;
      WebPage.displayListIBT(Mode.TAG_NUSED, request, str, enrPhoto.getID(),
			     WebPage.Box.MULTIPLE, Type.PHOTO) ;
      WebPage.displayListIBT(Mode.TAG_NONE, request, str, enrPhoto.getID(),
			     WebPage.Box.MULTIPLE, Type.PHOTO) ;
      str.append("		</td>\n"+
		 "	</tr>\n"+
		 "   </tr><tr>\n" +
		 "	<tr>\n"+
		 "	<td></td>"+
		 "		<td colspan='4' align='center'>\n");
      WebPage.displayUserPhoto(enrPhoto.getAlbum(), enrPhoto.getID(), str);
      str.append("		</td>\n"+
		 "	</tr>\n"+
		 "	<tr>\n"+
		 "		<td align='right'>Supprimer ?</td>\n" +
		 "		<td align='left' colspan='3'>"+
		 "<input type='text' "+
		 "name='suppr' size='33' maxlength='33'/>\n" +
		 "		\"Oui je veux supprimer cette photo\" </td>\n" +
		 "	</tr>\n"+
		 "	<tr>\n"+
		 "		<td align='right'>"+
		 "Representer l'album ?"+
		 "</td><td><input type='checkbox' "+
		 "name='represent' value='y' /></td>\n"+
		 "	</tr>\n");

      str.append("	<tr>\n"+
		 "<td align='right'>Representer le tag ?\n"+
		 "</td><td>");
      
      WebPage.displayListIBTNI(Mode.TAG_USED,
			     request,
			     str,
			     enrPhoto.getID(),
			     WebPage.Box.LIST,
			     Type.PHOTO,
			     "tagPhoto",
			     null);
      str.append("	</td></tr>\n");
      
      str.append("	<tr>\n"+
		 "		<td colspan='5' align='center'>"+
		 "<input type='submit' value='Valider' /></td>\n"+
		 "	</tr>\n"+
		 "</table>\n"+
		 "</form>\n"+
		 "<br/><br/>\n") ;
      str.append("<a href='"+from+"#"+enrPhoto.getID()+"'>"+
		 "Retour aux "+name+"</a>\n");
      output.append(str.toString());
    } catch (JDBCException e) {
      output.append("Impossible de modifier la photo ("+photoID+")"+
		    " => "+rq+"<br/>\n"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
    }
  }

  public static void displayPhoto(List list,
				     StringBuilder output,
				     HttpServletRequest request,
				     String message,
				     String pageGet,
				     String albmCount)
    throws HibernateException {
    WebPage.EditMode inEditionMode = WebPage.getEditionMode(request) ;
    String page = request.getParameter("page") ;
    
    String scount = request.getParameter("count") ;
    page = (page == null ? "0" : page) ;
    String all = request.getParameter("all") ;
    Boolean details = WebPage.getDetails (request) ;
    
    Integer[] bornes = WebPage.calculBornes(Type.PHOTO, page,
					    scount, list.size()) ;
    List portionLst = list.subList(bornes[0], bornes[1]) ;
    String degrees = "0" ;
    String tag = null ;
    String rq = null ;
    int countME = 0 ;
    boolean massEdit = false ;
    boolean reSelect = false ;
    boolean current = false ;
    
    String turn = request.getParameter("turn") ;
    if (inEditionMode == WebPage.EditMode.EDITION) {
      try {
	String action = request.getParameter("action") ;
	if ("MASSEDIT".equals(action)) {
	  if ("tag".equals(turn)) {
	    tag = StringUtil.escapeHTML(request.getParameter("newTag")) ;
	    rq = "from Tag where id = '"+tag+"'" ;
	    Tag enrTag = (Tag) WebPage.session.find(rq).iterator().next() ;
	    rq = "done" ;
	  } else if ("gauche".equals(turn)) {
	    degrees = "270" ;
	  } else if ("droite".equals(turn)) {
	    degrees = "90" ;
	  }

	  massEdit = true ;
	}
      } catch (JDBCException e) {
	output.append("Impossible d'executer la requete ...=> "+rq+"<br/>\n"+
		      e+"<br/>\n"+e.getSQLException()+"<br/>\n");
      } catch (NoSuchElementException e) {
	output.append("Impossible de trouver ce tag ("+tag+") ...<br/>\n");
	reSelect = true ;
      }
      output.append("<form action='"+pageGet+"&action=MASSEDIT&page="+bornes[3]+
		    "' method='POST'>\n");
    }
    
    output.append("<table>\n");
    Photo enrPhoto = null;
    int count = bornes[0] ;
    Iterator it = portionLst.iterator();
    while (it.hasNext()) {
      enrPhoto = (Photo) it.next();
      
      output.append("<tr><td colspan='3'></td></tr>\n");
      
      boolean reSelectThis = false ;
      if (massEdit) {
	String chkbox = request.getParameter("chk"+enrPhoto.getID()) ;
	if ("y".equals (all) || "modif".equals(chkbox)) {
	  current = true ;
	  try {
	    if ("tag".equals(turn)) {
	      String[] tags = null ;
	      
	      tags = new String[1] ;
	      tags[0] = tag ;
	      enrPhoto.addTags(tags);
	      output.append("Tag "+tag+" added to photo #"+enrPhoto.getID()+"!"+
			  "<br/><br/>\n") ;
	    } else {
	      if (!enrPhoto.rotate(degrees)) {
		output.append("Erreur dans le ConvertWrapper ..."+
			  "<br/><br/>\n") ;
		reSelectThis = true ;
	      }
	    }
	    
	    countME++ ;
	  } catch (Exception e) {
	    output.append("Impossible d'effectuer l'action sur cette photo..."+
			  e+"<br/><br/>\n") ;
	    reSelectThis = true ;
	  }
	}
      }
              
      if (message != null &&
	  enrPhoto.getID().toString().equals(request.getParameter("id"))) {
	output.append("<tr><td colspan='3'>"+message+"</td></tr>") ;
      }
      output.append("	<tr align='center' valign='middle'>\n");
      if (inEditionMode == WebPage.EditMode.EDITION) {
	output.append("<td><input type='checkbox' "+
		      "name='chk"+enrPhoto.getID()+"' "+
		      "value='modif' "+((reSelect || reSelectThis) && current ?
					"checked" : "")+
		      "/></td>\n");
      }
      
      output.append("		<td>\n" +
		    "                 <a name='"+enrPhoto.getID()+"'></a>"+
		    "			<a href='"+Path.LOCATION+".Images?"+
		    "id="+enrPhoto.getID()+"&mode=GRAND'>"+
		    "<img alt='"+enrPhoto.getDescription()+"' "+
		    "src='"+Path.LOCATION+".Images?"+
		    "id="+enrPhoto.getID()+"&mode=PETIT' /></a>\n" +
		    "		</td>\n");
      if (details) {
	output.append("	<td align='left' width='50%'><table><tr><td>"+
		      enrPhoto.getExif()
		      .replace("\n", "</td></tr><tr><td>\n")
		      .replace(" - ", "</td><td>")+
		      "</td></tr></table></td>\n");
      }
      output.append("		<td width='50%' align='left'>\n" +
		    "		<table>\n" +
		    "			<tr>\n" +
		    "				<td>"+
		    enrPhoto.getDescription()+"</td>\n" +
		    "			</tr>\n" +
		    "			<tr>\n" +
		    "				<td>\n" +
		    "					<i>");
      WebPage.displayListIBT(Mode.TAG_USED, request, output, enrPhoto.getID(),
			     WebPage.Box.NONE, Type.PHOTO);
      output.append("</i>\n" +
		    "				</td>\n" +
		    "			</tr>\n");
		    
      //liste des utilisateurs pouvant voir cette photo
      if (WebPage.isLoggedAsCurrentManager(request)
	  && !WebPage.isRootSession(request)) {
	if (inEditionMode != WebPage.EditMode.VISITE) {
	  output.append("			<tr>\n" +
			"				<td>");
	  WebPage.displayListIBT(Mode.USER, request, output, enrPhoto.getID(),
				 WebPage.Box.NONE, Type.PHOTO);
	  output.append("</td>\n" +
			"			</tr>\n");
	}
      }
      output.append("		</table>\n" +
		    "		</td>\n");
      //lien vers la page d'edition
      if (WebPage.isLoggedAsCurrentManager(request)
	  && inEditionMode == WebPage.EditMode.EDITION
	  && !WebPage.isRootSession(request)) {
	output.append("		<td width='5%'>"+
		      "<a href='"+Path.LOCATION+".Photos?action=EDIT"+
		      "&id="+enrPhoto.getID()+
		      "&count="+count+
		      "&albmCount="+albmCount+"'>"+
		      "<img src='/data/web/edit.png' width='25' heigh='25'>"+
		      "</a></td>\n");
      }
      output.append("	</tr>\n");
      current = false ;
      count++ ; 
    }
    output.append("</table>\n");
    
    if (inEditionMode == WebPage.EditMode.EDITION) {
      if (massEdit) {
	output.append("<i>");
	if (countME == 0 || "rien".equals(turn))
	   output.append("Aucune modification faite ... ?");
	else 
	  output.append(countME+" photo"+
			(countME == 1 ? " a été modifiée" :
			                "s ont été modifées"));
	output.append("</i><br/><br/>\n") ;
      }
      output.append("<input type='checkbox' name='all' value='y' />\n"+
		    "Toutes <br /><br />\n");
      output.append("<input type='radio' name='turn' value='droite' />\n"+
		    "Tourner vers la droite <br />\n");
      output.append("<input type='radio' name='turn' value='gauche' />\n"+
		    "Tourner vers la gauche <br />\n");
      output.append("<input type='radio' name='turn' value='tag' checked />\n"+
		    "Tagger avec ");
      WebPage.displayListBN(WebPage.Mode.TAG_USED, request, output,
			    WebPage.Box.LIST, "newTag") ;
      output.append("<br /><input type='submit' value='OK'/>\n") ;
      output.append("</form>\n");
    }
    WebPage.displayPages(pageGet, bornes, output);
  }

}