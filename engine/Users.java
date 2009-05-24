package engine;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Iterator ;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constante.Path;
import entity.Theme;
import entity.Utilisateur;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import util.StringUtil;

public class Users extends HttpServlet {
  private static final long serialVersionUID = 1L;
  
  public void init() {
    Path.setLocation(this) ;
  }
  
  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    WebPage.treat(WebPage.Page.USR, request, response) ;
  }
  public void doPost(HttpServletRequest request,
			   HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }

  public static void treatUSR(HttpServletRequest request,
			 StringBuilder output)
    throws HibernateException {
    String action = request.getParameter("action") ;
    String theme = WebPage.getThemeID(request);
    String rq = null ;
    engine.Users.clearUser(request) ;
    try {
      if ("LOGIN".equals(action)) {
	String userName =
	  StringUtil.escapeHTML(request.getParameter("userName")) ;
	
	output.append("<b>WebAlbums de "+
		      WebPage.getThemeName(request)+
		      "</b><br/><br/>\n");
	boolean asThemeManager =
	  (userName != null &&
	   userName.equals(WebPage.getThemeName(request))) ;
	boolean needPass = false ;
	String id = null ;
	
	if (!asThemeManager) {
	  rq = "from Utilisateur where nom = '"+userName+"'" ;
	  List list = WebPage.session.find(rq) ;
	  rq = "done" ;
	  Iterator it = list.iterator() ;
	  if (it.hasNext()) {
	    Utilisateur enrUtil = (Utilisateur) it.next() ;
	    //s'il n'y a pas besoin de mot de passe
	    if (saveUser(request, enrUtil, null)) {
	      //on continue vers les choix
	      output.append("<i> Acces "+enrUtil.getNom()+" </i><br/>\n");
	      Choix.treatCHX(request, output) ;
	    } else {
	      needPass = true ;
	      id = enrUtil.getID().toString() ;
	    }
	  } else {
	    output.append("<i> Aucun utilisateur de ce nom ... "+
			  "("+userName+" </i><br/>\n") ;
	    action = null ;
	  }
	} else /*connexion en tant que manager du theme*/{
	  needPass = true ;
	  rq = "from Theme where id = '"+theme+"'" ;
	  List list = WebPage.session.find(rq) ;
	  rq = "done" ;
	  Theme aut = (Theme) list.iterator().next() ;
	  id = aut.getID().toString();
	}
	
	if (needPass) {
	  output.append("<b>Mot de passe requis pour l'utilisateur "+
			"<b>"+userName+"</b> : </b></br></br>\n"+
			"<form action='"+Path.LOCATION+".Users?"+
			"action=CONFIRM&userID="+id+
			"&asThemeManager="+asThemeManager+"' method='post'>\n" +
			"	<input type='password' name='userPasswd' "+
			"size='20' maxlength='20'/>\n" +
			"	<input type='submit' value='OK'/>\n" +
			"</form>\n");
	}
      } else if ("CONFIRM".equals(action)) {
	//verification du mot de passe
	String userID = StringUtil.escapeHTML(request.getParameter("userID")) ;
	String passwd = request.getParameter("userPasswd") ;
	//login theme manager
	if (Boolean.parseBoolean(request.getParameter("asThemeManager"))) {
	  if (saveUser(request, null, passwd)) {
	    
	    output.append("<i>Acces Theme manager </i><br/>\n");
	    
	    Choix.treatCHX(request, output) ;
	    return ;
	  } else {
	    WebPage.stat.warn("Incorrect password...") ;
	    output.append("<i>Mot de passe incorrect ... </i><br/>\n");
	    action = null ;
	  }
	} else /*login utilisateur classique*/{
	  rq = "from Utilisateur where id = '"+userID+"'" ;
	  List list = WebPage.session.find(rq) ;
	  rq = "done" ;
	  Iterator it = list.iterator() ;
	  if (it.hasNext()) {
	    Utilisateur enrUtil = (Utilisateur) it.next() ;
	    saveUser(request,enrUtil, passwd) ;
	    Choix.treatCHX(request, output) ;
	  }
	  
	}
      } else /*si on est ni en LOGIN ni en CONFIRM, on reaffiche le choix utilisateur*/{
	action = null ;
			}
    } catch (JDBCException e) {
      output.append("<i>Impossible d'effectuer la requete </i>=> "+rq+"<br/>\n"+
		    e+"<br/>\n"+e.getSQLException()+"<br/>\n");
    } catch (NoSuchElementException e) {
      output.append("<i>Cet utilisateur n'existe pas ...  </i><br/>\n");
      action = null ;
    }
    
    if (action == null) {
      //si on ne peut pas sauver le theme manager, on reaffiche le menu themes
      if (WebPage.tryToSaveTheme(request, output, theme) == null) {
	display.VoidPage.treatVOID(output) ;
	return ;
      }
      output.append("<b>Nom d'utilisateur : </b></br></br>\n"+
		    "<form action='"+Path.LOCATION+".Users' "+
		    "method='get'>\n" +
		    "	<input type='hidden' name='action' value='LOGIN'/>" +
		    "	<input type='input' name='userName' size='20' "+
		    "maxlength='20'/>\n" +
		    "	<input type='submit' value='OK'/>\n" +
		    "</form>\n");
    }
		
    output.append("<a href='"+Path.LOCATION+".WebPage'>"+
		  "Retour aux themes</a>\n");
  }
      
	
  protected static void clearUser (HttpServletRequest request) {
    request.getSession().setAttribute("LogInID", null) ;
  }
  
  protected static boolean saveUser(HttpServletRequest request,
				    Utilisateur enrUser,
				    String passwd)
    throws HibernateException {
    String rq = null ;
    String userID ;
    String goodPasswd ;
    
    if (enrUser == null) {
      String themeID = WebPage.getThemeID(request) ;
      
      rq = "from Theme where id = '"+themeID+"'" ;
      List list = WebPage.session.find(rq) ;
      rq = "done" ;
      Theme util = (Theme) list.iterator().next() ;

      userID = WebPage.USER_CHEAT ;
      goodPasswd = util.getPassword() ;
      
    } else {
      userID = Integer.toString(enrUser.getID()) ;
      goodPasswd = enrUser.getPassword() ;
    }

    if (goodPasswd != null && !goodPasswd.equals(passwd)) {
      return false ;
    }
    
    request.getSession().setAttribute("LogInID", userID) ;
    request.getSession().setAttribute("inEditionMode", false) ;
    return true ;
  }
  
  public static String getUser(HttpServletRequest request) {
    String user = (String) request.getSession().getAttribute("LogInID") ;
    if (user == null) {
      user = StringUtil.escapeHTML(request.getParameter("user"));
    }
    return user ;
  }
}
