package engine;

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

public class Albums extends HttpServlet {
  private static final long serialVersionUID = 1L;
  
  public void init() {
    Path.setLocation(this) ;
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    engine.WebPage.treat(engine.WebPage.Page.ALBM, request, response) ;
  }
  public void doPost(HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }
  
  public static void treatALBM(HttpServletRequest request,
				  StringBuilder output)
    throws HibernateException {
    engine.WebPage.log.warn("Traitement Album");
    String action = request.getParameter("action") ;
		
    if ("SUBMIT".equals(action)
	&& WebPage.isLoggedAsCurrentManager(request, output)
	&& !WebPage.isRootSession(request)) {
      action = treatAlbmSUBMIT(request, output);
    }
    
    if ("EDIT".equals(action)
	&& WebPage.isLoggedAsCurrentManager(request, output)
	&& !WebPage.isRootSession(request)) {
      display.Albums.treatAlbmEDIT(request, output);
    } else {
      //memoriser les params de lURL pour pouvoir revenir
      String from = Path.LOCATION+".Albums" ;
      request.getSession().setAttribute("from", from) ;
      
      output.append(WebPage.getHeadBand(request));
      //sinon afficher la liste des albums de ce theme
      treatAlbmDISPLAY(request, output, action);
    }
  }
  
  public static String treatAlbmSUBMIT(HttpServletRequest request,
					  StringBuilder output)
    throws HibernateException {
    String albumID = StringUtil.escapeHTML(request.getParameter("id")) ;
    String rq = null;
    try {
      rq = "from Album "+
	"where id = '"+albumID+"' "+
	"and id in ("+WebPage.listAlbumAllowed(request)+")" ;
      List list = WebPage.session.find(rq);
      rq = "done" ;
      String suppr = request.getParameter("suppr") ;
      if ("Oui je veux supprimer cet album".equals(suppr)) {
	FilesFinder.deleteAlbum (albumID, output) ;
	return null ;
      }
      Album enrAlbum = (Album) list.iterator().next() ;
      
      String desc = StringUtil.escapeHTML(request.getParameter("desc")) ;
      String nom = StringUtil.escapeHTML(request.getParameter("nom")) ;
      String date = request.getParameter("date") ;
      String[] tags = request.getParameterValues("tags") ;
      String[] users = request.getParameterValues("users") ;
      String force = request.getParameter("force") ;
      
      enrAlbum.setTagsToPhoto(tags, "yes".equals(force)) ;
      enrAlbum.setUsers(users) ;
      enrAlbum.setNom(nom) ;
      enrAlbum.setDescription(desc) ;
      enrAlbum.setDate(date) ;
      
      return "<i> Album ("+enrAlbum.getID()+") "+
	"correctement mise à jour !</i><br/><br/>\n" ;
    } catch (JDBCException e) {
      output.append("Impossible de finaliser la modification de l'album "+
		    "("+albumID+") => "+e.getSQLException()+"<br/>\n"+
		    "Derniere requete : "+rq+"<br/>\n"+e+"<br/>\n");
      WebPage.log.warn("Impossible de finaliser la modification de l'album "+
		       "("+albumID+") => "+e+"<br/>\n");
      return "EDIT" ;
    } catch (NoSuchElementException e) {
      output.append("Impossible d'acceder à cet album ("+albumID+")...<br/>\n");
      return "EDIT" ;
    } catch (WebPage.AccessorsException e) {
      output.append("Problème dans l'acces aux champs ... ("+e+")...<br/>\n");
      return "EDIT" ;
    }
  }

  public static void treatAlbmDISPLAY(HttpServletRequest request,
					 StringBuilder output,
					 String message)
    throws HibernateException {
    String theme = WebPage.getThemeID(request) ;
    String rq = null ;
    String pageGet = Path.LOCATION+".Albums?" ;
    try {
      rq = "from Album "+
	"where id in ("+engine.WebPage.listAlbumAllowed(request)+") " ;
      if (!engine.WebPage.isRootSession(request)) {
	rq += "and theme = '"+theme+"' " ;
      }
      rq += "order by date desc " ;
      
      List list = engine.WebPage.session.find(rq);
      rq = "done" ;
      
      Iterator it = list.iterator () ;
      if (it.hasNext()) {
	output.append("<center><h1>"+engine.WebPage.getThemeName(request)+"</h1></center>");
	display.Albums.displayAlbum(list, output, request, message, pageGet);
	
      } else {
	output.append("No album to display...<br/>");
      }
      output.append("<br/>\n");
      
      output.append("<a href='"+Path.LOCATION+".Choix'>"+
		    "Retour aux choix</a>\n");
    } catch (JDBCException e) {
      output.append("<br/><i>Impossible d'afficher les albums du theme "+
		    "("+theme+") </i>=> "+rq+"<br/>\n"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
    }
  }
  
  public static void displayListUserInside(HttpServletRequest request,
					    StringBuilder output, int id)
    throws HibernateException {
    String rq = null ;
    try {
      rq = "from Utilisateur util " +
	"where util.ID in ( " +
	//  liste des utilisateurs autorisé à voir les photos d'un album
	"   select u.ID " +
	"   from Utilisateur u " +
	"   where ("+
	"     u.ID not in ("+
	"select up.User from UserPhoto up, Photo p "+
	"where p.ID = up.Photo and p.Album = '"+id+"')" +
	"     and u.ID in ("+
	"select ua.User from UserAlbum ua "+
	"where ua.Album = '"+id+"')" +
	"     ) or (" +
	"	  u.ID in ("+
	"select up.User from UserPhoto up, Photo p "+
	"where p.ID = up.Photo and p.Album = '"+id+"')" +
	"     and u.ID not in ("+
	"select ua.User from UserAlbum ua "+
	"where ua.Album = '"+id+"')" +
	"	)" +
	") and util.ID not in (" +
	//	liste des utilisateur autorisé à voir un album
	"	select ua.User from UserAlbum ua where ua.Album = '"+id+"'"+
	")" ;
      List list = engine.WebPage.session.find(rq);
      rq = "done" ;
      List<Integer> ids = new ArrayList<Integer>(list.size()) ;
      for (Object o : list) {
	Utilisateur u = (Utilisateur) o ;
	ids.add(u.getID()) ;
      }
      StringBuilder str = new StringBuilder () ;
      engine.WebPage.displayListLB(engine.WebPage.Mode.USER, request, str, ids,
			    engine.WebPage.Box.NONE) ;
      if (str.length() != 0) {
	output.append("  ("+str.toString()+")") ;
      }
    } catch (Exception e) {
      output.append ("error ..."+e);

    }
    
  }
  
}
