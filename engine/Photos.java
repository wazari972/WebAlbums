package engine;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constante.Path;

import engine.WebPage ;
import engine.WebPage.*;
import entity.*;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Query;

import util.FilesFinder;
import util.StringUtil;

import org.hibernate.Transaction;

public class Photos {
  private static final long serialVersionUID = 1L;
    
  public static void treatPHOTO(HttpServletRequest request,
				StringBuilder output)
    throws WebPage.AccessorsException, HibernateException {
    String action = request.getParameter("action") ;
    
    if ("SUBMIT".equals(action) &&
	WebPage.isLoggedAsCurrentManager(request, output)
	&& !WebPage.isReadOnly()) {
      action = treatPhotoSUBMIT(request, output);
      //ensuite afficher la liste *normal* des photos
      //s'il n'y a pas eu de probleme
    }
    
    if ("EDIT".equals(action)
	&& WebPage.isLoggedAsCurrentManager(request)
	&& !WebPage.isReadOnly()) {
      display.Photos.treatPhotoEDIT(request, output);
      
    } else {
      output.append(WebPage.getHeadBand(request));
      treatPhotoDISPLAY(request, output, action);
    }
  }

	
  protected static String treatPhotoSUBMIT(HttpServletRequest request,
					   StringBuilder output)
    throws WebPage.AccessorsException, HibernateException {
    String photoID = StringUtil.escapeHTML(request.getParameter("id")) ;
    String theme = WebPage.getThemeID(request) ;
    String rq = null ;
    Transaction tx = WebPage.session.beginTransaction();
    try {
      rq = "select ph from Album al, Photo ph " +
	"where al.ID = ph.Album " +
	"and al.Theme = '"+theme+"' " +
	"and ph.ID = '"+photoID+"'" ;
      Photo enrPhoto = (Photo) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;
      
      if (enrPhoto == null) {
	output.append("<i> Impossible de trouver cette photo "+
		      "("+photoID+"/"+theme+")</i><br/>\n");
	return null ;
      }
      
      String suppr = request.getParameter("suppr") ;
      if ("Oui je veux supprimer cette photo".equals(suppr)) {
	
	String msg = FilesFinder.deletePhoto (photoID, output) ;
	output.append("<i>"+msg+"</i><br/>\n");
	return null ;
      }
      
      String desc = StringUtil.escapeHTML(request.getParameter("desc")) ;
      enrPhoto.setDescription(desc) ;
      
      String[] tags = request.getParameterValues("tags");
      enrPhoto.setTags(tags) ;
      
      rq = "from Utilisateur" ;
      List list = WebPage.session.createQuery(rq).list();
      rq = "done" ;
      
      String[][] users = new String[list.size()][2] ;
      Iterator it = list.iterator() ;
      for (int i = 0; it.hasNext(); i++) {
	Utilisateur user = (Utilisateur) it.next();
	users[i][0] = user.getID().toString() ;
	users[i][1] =
	  StringUtil.escapeHTML(request.getParameter("user"+user.getID())) ;
      }
      enrPhoto.setUsers (users) ;
      
      String represent = request.getParameter("represent") ;
      if ("y".equals(represent)) {
	rq = "from Album where id = '"+enrPhoto.getAlbum()+"'" ;
	Album enrAlbum = (Album)  WebPage.session.createQuery(rq).uniqueResult();
	rq = "done" ;
	
	if (enrAlbum == null) {
	  output.append("Impossible d'accerder l'album à representer "+
			"("+enrPhoto.getAlbum()+")<br/>"+rq+"<br/>\n");
	} else {
	  enrAlbum.setPicture(Integer.toString(enrPhoto.getID()));
	}
      }

      String tagPhoto = request.getParameter("tagPhoto") ;
      if (!"-1".equals(tagPhoto)) {
	rq = "from TagTheme "+
	  "where theme = "+WebPage.getThemeID(request) +
	  " and tag = " + tagPhoto;
	output.append(rq);
	TagTheme enrTagTh =
	  (TagTheme)  WebPage.session.createQuery(rq).uniqueResult() ;
	rq = "done" ;
	
	if (enrTagTh != null) {
	  enrTagTh.setPhoto(enrPhoto.getID()) ;
	} else {
	  enrTagTh = new TagTheme() ;
	  enrTagTh.setTheme (WebPage.getThemeID(request)) ;
	  enrTagTh.setTag (tagPhoto) ;
	  
	  enrTagTh.setPhoto (enrPhoto.getID());
	}
	WebPage.session.saveOrUpdate(enrTagTh);
      }
      tx.commit();
      
      return "<b> Photo ("+enrPhoto.getID()+") "+
	"correctement mise à jour !</b><br/><br/>\n";
    } catch (JDBCException e) {
      output.append("Impossible d'effectuer la modification de la photo "+
		    "("+photoID+") => "+rq +"<br/>\n"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
      tx.rollback();
      return "EDIT" ;
    } catch (NoSuchElementException e) {
      output.append("Impossible d'accerder à la photo à modifier "+
		    "("+photoID+")<br/>"+rq+"<br/>\n");
      tx.rollback();
      return "EDIT" ;
    }
  }	
	
  protected static void treatPhotoDISPLAY(HttpServletRequest request,
					  StringBuilder output,
					  String message)
    throws HibernateException {
    //afficher les photos
    //afficher la liste des albums de cet theme
    String album = StringUtil.escapeHTML(request.getParameter("album")) ;
    String page = StringUtil.escapeHTML(request.getParameter("page")) ;
    String albmCount = StringUtil.escapeURL(request.getParameter("albmCount")) ;
    page = (page == null ? "0" : page);
    
    Album enrAlbum = null ;
    String rq = null ;
    try {
      rq = "from Album " +
	"where id = '"+album+"' "+
	"and id in ("+WebPage.listAlbumAllowed(request)+")" ;
      
      enrAlbum = (Album) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;
      
      if (enrAlbum == null) {
	output.append("L'album ("+album+") n'existe pas dans la base "+
		      "ou n'est pas accessible...<br/>\n") ;
	return ;
      }
      
      output.append("<b><center>"+enrAlbum.getNom()+"</center> </b></br>\n"+
		    "<i><center>"+enrAlbum.getDescription()+"</center>"+
		    "</i></br>\n");
      
      
      rq = "from Photo " +
	"where album = '"+album+"' " +
	"and id in ("+WebPage.listPhotoAllowed(request)+")"+
	"order by path" ;
      
      Query query = WebPage.session.createQuery(rq);
      query.setReadOnly(true).setCacheable(true) ;
      String pageGet = ""+Path.LOCATION+"Photos?"+
	"album="+album+"&albmCount="+albmCount ;
      display.Photos.displayPhoto(query, output, request, message, pageGet, albmCount);
			
      output.append("<br/>\n");
      String from = ""+Path.LOCATION+"Albums" ;
      output.append("<a href='"+from+"?count="+albmCount+"#"+album+"'>"+
		    "Retour aux albums</a>\n");
      
    } catch (NullPointerException e) {
      output.append("Quelque chose n'existe pas ... "+e+"<br/>\n") ;
    } catch (JDBCException e) {
      output.append("Il y a une erreur dans la requete : "+rq+"<br/>\n"+
		    e+"<br/>\n"+e.getSQLException()+"<br/>\n") ;
    }
  }
}
