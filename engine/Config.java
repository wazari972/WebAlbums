package engine ;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import constante.Path;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import traverse.FilesFinder;
import util.HibernateUtil;
import util.StringUtil;
import engine.WebPage.Mode;
import entity.Theme;
import entity.Photo;
import entity.Tag;
import entity.Utilisateur;
import entity.Geolocalisation ;
import entity.TagType ;

public class Config extends HttpServlet {
  private static final long serialVersionUID = -628341734743684910L;
  
  public void init() {
    Path.setLocation(this) ;
  }
  public void doPost(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    WebPage.treat(WebPage.Page.CONFIG, request, response) ;
  }
  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    WebPage.treat(WebPage.Page.CONFIG, request, response) ;
  }

  public static void treatCONFIG(HttpServletRequest request,
				    StringBuilder output)
    throws HibernateException {
    display.Config.displayCONFIG(request, output) ;
  }

  public static void treatIMPORT(HttpServletRequest request,
				  String theme,
				  String passwrd,
				  StringBuilder output)
    throws HibernateException {
    String rq = null ;
    try {
      //Session session = HibernateUtil.currentSession();
      //rq = "from Theme where nom = '"+theme+"'" ;
      //List list  = session.find(rq) ;
      //rq = "done" ;
      //if (!list.isEmpty()) {
	output.append("<i>Begining ...</i><br/>\n") ;
	boolean ret = new FilesFinder ()
	  .importAuthor(request, theme, passwrd, output);

	if (ret)
	  output.append("<i>Well done !</i><br/>\n") ;
	else
	  output.append("<i>An error occured ...</i><br/>\n") ;
	//} else {
	//output.append("<i>Impossible de trouver ce theme ("+theme+") "+
	//	      "...</i>\n");
    //}
    } catch (JDBCException e) {
      output.append("<i> Impossible d'effectuer la requete' :</i>"+
		    rq+"<br/>"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
    }
    output.append("<br/><br/>\n");
  }
  
  public static void treatMODTAG(HttpServletRequest request,
				 StringBuilder output)
    throws HibernateException {
    String rq = null ;
    
    String nouveau = StringUtil.escapeHTML(request.getParameter("nouveau")) ;
    String tags = StringUtil.escapeHTML(request.getParameter("tags")) ;

    if ("-1".equals(tags)) {
      output.append("<i> Pas de tag selectionné ...</i></br/>\n");
      return ;
    }

    Transaction tx = WebPage.session.beginTransaction();
    
    try {
      rq = "from Tag where id = '"+tags+"'" ;
      Tag tag = (Tag) WebPage.session.find(rq).iterator().next() ;
      rq = "from Tag where nom = '"+nouveau+"'" ;
      if (!WebPage.session.find(rq).iterator().hasNext()) {
	tag.setNom (nouveau) ;
	WebPage.session.update(tag) ;
	
	output.append("<i> Tag correctement renomée en "+nouveau+"</i></br/>\n");
	tx.commit() ;
      } else {
	output.append("<i> Le Tag "+nouveau+
		      " est déjà présent dans la base ...</i></br/>\n");
	tx.rollback() ;
      }

    } catch (JDBCException e) {
      if (e.getSQLException().toString().contains("Duplicate")) {
	//should never be reacht, a test have been performed 
	output.append("<i>Le tag </i>'"+nouveau+"'"+
		      "<i> est déjà dans la base ...</i>"+
		      "<br/><br/>\n");
      } else {
	output.append("<i> Impossible d'effectuer la requete' :</i>"+
		      rq+"<br/>"+e+"<br/>\n"+
		      e.getSQLException()+"<br/>\n");
      }
      tx.rollback() ;
    } catch (NoSuchElementException e) {
      output.append("<i>Impossible de trouver ce tag ("+e+") ...</i>\n");
      tx.rollback() ;
    }
  }

    public static void treatMODGEO(HttpServletRequest request,
				  StringBuilder output)
    throws HibernateException {
    String rq = null ;
    
    String lng = StringUtil.escapeHTML(request.getParameter("lng")) ;
    String lat = StringUtil.escapeHTML(request.getParameter("lat")) ;
    String tags = StringUtil.escapeHTML(request.getParameter("tags")) ;

    if ("-1".equals(tags)) {
      output.append("<i> Pas de tag selectionné ...</i></br/>\n");
      return ;
    }

    Transaction tx = WebPage.session.beginTransaction();
    
    try {
      rq = "from Geolocalisation where tag = '"+tags+"'" ;
      Geolocalisation enrGeo =
	(Geolocalisation) WebPage.session.find(rq).iterator().next() ;
      
      enrGeo.setLong (lng) ;
      enrGeo.setLat(lat);
      WebPage.session.update(enrGeo) ;
      
      output.append("<i> Localisation correctement mise à jour "+
		    "à "+lng+"/"+lat+"</i></br/>\n");
      tx.commit() ;
      

    } catch (JDBCException e) {
      output.append("<i> Impossible d'effectuer la requete' :</i>"+
		    rq+"<br/>"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
      
      tx.rollback() ;
    } catch (NoSuchElementException e) {
      output.append("<i>Impossible de trouver cette geoloc ("+e+") ...</i>\n");
      tx.rollback() ;
    }
  }
  
  public static void treatNEWTAG(HttpServletRequest request,
				  StringBuilder output)
    throws HibernateException {
    String rq = null ;
    String nom = StringUtil.escapeHTML(request.getParameter("nom")) ;
    String type = StringUtil.escapeHTML(request.getParameter("type")) ;

    if ("-1".equals(type)) {
      output.append("<i> Pas de type selectionné ...</i></br/>\n");
      return ;
    }

    Transaction tx = WebPage.session.beginTransaction();
    Tag t = null ;
    try {
      if (nom != null && !nom.equals("")) {
	String msg = "";
	String liste = "" ;
	
	rq = "from Tag where nom = '"+nom+"'" ;
	Iterator it  = WebPage.session.find(rq).iterator() ;
	rq = "done" ;
	if (!it.hasNext()) {
	  int itype = Integer.parseInt (type) ;
	  if (0 > itype || itype > 3) {
	    output.append("<i> Type incorrect ("+type+") ...</i><br/><br/>\n");
	  }
	  t = new Tag () ;
	  
	  t.setNom(nom) ;
	  t.setTagType(itype);
	  WebPage.session.save(t) ;
	  output.append("<i> TAG ==> "+t.getID()+" <==<br/>\n");
	  if (Integer.parseInt(type) == 3) {
	    try {
	      String longit = StringUtil.escapeHTML(request.getParameter("long")) ;
	      String lat = StringUtil.escapeHTML(request.getParameter("lat")) ;
	      
	      Geolocalisation geo = new Geolocalisation () ;
	      geo.setTag(t.getID ());
	      geo.setLong(longit);
	      geo.setLat (lat);
	      
	      WebPage.session.save(geo);
	      msg = " ("+longit+"/"+lat+")" ;
	    } catch (JDBCException e) {
	      if (e.getSQLException().toString().contains("Duplicate")) {
		output.append("<i>Erreur dans l'insertion de la "+
			      "geolocalisation ...</i><br/><br/>"+
			      e.getSQLException()+"<br/><br/>\n");
	      } else {
		output.append("<i> Impossible d'effectuer la requete' :</i>"+rq+
			      "<br/>"+e+"<br/>\n"+e.getSQLException()+"<br/>\n");
	      }
	      tx.rollback();
	      return ;
	    }
	  }
	  tx.commit();
	  
	  switch (Integer.parseInt(type)) {
	    case 1 : liste = "Where" ; break ;
	    case 2 : liste = "What"  ; break ;
	    case 3 : liste = "Where" ; break ;
	  }
	  
	  output.append("<i>Tag</i> '"+nom+msg+"'"+
			"<i> correctement ajouté à la liste "+liste+
			"</i><br/><br/>\n");
	} else {
	  output.append("<i> Le Tag "+nom+
			" est déjà présent dans la base ...</i></br/>\n");
	  while (it.hasNext()) {
	    Tag enrTag = (Tag) it.next() ;
	    output.append("<i>"+enrTag.getID()+" - "+enrTag.getNom()+"</i><br/>\n");
	  }
	  tx.rollback() ;
	}
      } else {
	output.append("<i>Le nom du tag est vide ...</i><br/><br/>\n");
      }
    } catch (JDBCException e) {
      output.append("<i> Impossible d'effectuer la requete' :</i>"+rq+
		    "<br/>"+e+"<br/>\n"+e.getSQLException()+"<br/>\n");
      
      tx.rollback();
      try {
	if (t != null) {
	  output.append("<i> Suppression de l'enr "+
			t.getID()+" crée par erreur</i></br/>\n");
	  WebPage.session.delete(t);
	}
      }catch (JDBCException e2) {
	output.append("<i> Impossible d'effectuer la requete' :</i>"+rq+
		      "<br/>"+e2+"<br/>\n"+e.getSQLException()+"<br/>\n");
	
      }
    } catch (HibernateException e) {
      output.append("<i>Problème Hibernate : "+e+"</i><br/><br/>");
      tx.rollback();
    } catch (NumberFormatException e) {
      output.append("<i>Erreur dans le cast de l'un des nombres : "+e
		    +"</i><br/><br/>");
      tx.rollback();
    }
  }
  public static List<Integer> treatDELTAG(HttpServletRequest request,
					   StringBuilder output)
    throws HibernateException {
    String rq = null ;
    String[] tags = request.getParameterValues("tags");
    String sure = request.getParameter("sure") ;
    
    Transaction tx = WebPage.session.beginTransaction();
    try {
      if (tags != null &&
	  String.CASE_INSENSITIVE_ORDER.compare(sure, "yes") == 0) {
	
	//liens Tag->Photos
	rq = "select photo from TagPhoto photo, Tag tag "+
	  "where tag.ID in ( '-1'" ;
	for (int i = 0; i < tags.length; i++) {
	  rq += ", '"+tags[i]+"' " ;
	}
	rq += ") and tag.ID = photo.Tag";
	List list = WebPage.session.find(rq) ;
	rq = "done" ;
	output.append("<i> Suppression de "+list.size()+" Tags Photo</i>"+
		      "<br/>\n");
	Iterator it = list.iterator();
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	}
	
	//liens Tag->Localisation
	rq = "from Geolocalisation where tag in ( '-1'" ;
	for (int i = 0; i < tags.length; i++) {
	  rq += ", '"+tags[i]+"' " ;
	}
	rq += ")";
	list = WebPage.session.find(rq) ;
	rq = "done" ;
	output.append("<i> Suppression de "+list.size()+" "+
		      "Geolocalisations</i><br/>\n");
	it = list.iterator();
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	}
	
	//tags
	rq = "from Tag where ID in ( '-1'" ;
	for (int i = 0; i < tags.length; i++) {
	  rq += ", '"+tags[i]+"' " ;
	}
	rq += ")";
	list = WebPage.session.find(rq) ;
	rq = "done" ;
	output.append("<i> Suppression de "+list.size()+" Tags</i><br/>\n");
	it = list.iterator();
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	}
	tx.commit();
	output.append("<br/>\n");
	      return null ;
      } else {
	if (tags != null) {
	  output.append("<i> vous n'êtes pas sûr ("+sure+") ?</i><br/><br/>\n");
	  List<Integer> l = new ArrayList<Integer>(1) ;
	  l.add(Integer.parseInt(tags[0]));
	  return l ;
	} else {
	  output.append("<i> Aucun tag selectionné ...</i><br/><br/>\n");
	  return null ;
	}
      }
    } catch (JDBCException e) {
      output.append("<i> Impossible d'effectuer la requete' :</i>"+rq+"<br/>\n"+
		    e+"<br/>\n"+e.getSQLException()+"<br/>\n");
      return null ;
    } catch (HibernateException e) {
      output.append("<i>Problème Hibernate : "+e+"</i><br/><br/>");
      tx.rollback();
      return null;
    }
  }
  
  public static void treatNEWUSER(HttpServletRequest request,
				   StringBuilder output)
    throws HibernateException {
    Transaction tx = null ;
    String rq = null ;
    try {
      tx = WebPage.session.beginTransaction();
      
      Utilisateur t = new Utilisateur () ;
      String nom = StringUtil.escapeHTML(request.getParameter("nom")) ;
      String pass = StringUtil.escapeHTML(request.getParameter("pass")) ;
      
      if (nom != null && !nom.equals("")) {
	t.setNom(nom) ;
	t.setPassword(("".equals(pass) ? null : pass)) ;
	WebPage.session.save(t) ;
	
	output.append("<i>Utilisateur </i>'"+nom+"'"+
		      "<i> correctement ajouté</i><br/><br/>\n");
	tx.commit();
      } else {
	output.append("<i>Le nom de l'utilisateur est vide ...</i>"+
		      "<br/><br/>\n");
      }
    } catch (JDBCException e) {
      output.append("<i> Impossible d'effectuer la requete' :</i>"+
		    rq+"<br/>\n"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
    } catch (HibernateException e) {
      output.append("<i>Problème Hibernate : "+e+"</i><br/><br/>");
      tx.rollback();
    }
  }
  public static void treatDELUSER(HttpServletRequest request,
				   StringBuilder output)
    throws HibernateException {
    String rq;
    String[] users = request.getParameterValues("users");
    String sure = request.getParameter("sure") ;
    if (users != null &&
	String.CASE_INSENSITIVE_ORDER.compare(sure, "yes") == 0) {
      rq = null;
      Transaction tx = WebPage.session.beginTransaction();
      try {
	rq = "select albm from UserAlbum albm, Utilisateur user "+
	  "where user.ID in ( '-1'" ;
	for (int i = 0; i < users.length; i++) {
	  rq += ", '"+StringUtil.escapeHTML(users[i])+"' " ;
	}
	rq += ") and user.ID = albm.User";
	List list = WebPage.session.find(rq) ;
	rq = "done" ;
	output.append("<i> Suppression de "+list.size()+" "+
		      "Users Album</i><br/>\n");
	Iterator it = list.iterator();
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	}
	
	rq = "select photo from UserPhoto photo, Utilisateur user where "+
	  "user.ID in ( '-1'" ;
	for (int i = 0; i < users.length; i++) {
	  rq += ", '"+users[i]+"' " ;
	}
	rq += ") and user.ID = photo.User";
	list = WebPage.session.find(rq) ;
	rq = "done" ;
	output.append("<i> Suppression de "+list.size()+" "+
		      "Users Photo</i><br/>\n");
	it = list.iterator();
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	}
	
	rq = "from Utilisateur where ID in ( '-1'" ;
	for (int i = 0; i < users.length; i++) {
	  rq += ", '"+users[i]+"' " ;
	}
	rq += ")";
	list = WebPage.session.find(rq) ;
	rq = "done" ;
	output.append("<i> Suppression de "+list.size()+" utilisateur"+
		      "</i><br/>\n");
	it = list.iterator();
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	}
	tx.commit();
	output.append("<br/>\n");
      }catch (JDBCException e) {
	tx.rollback();
	output.append("<i> Impossible d'effectuer la manip' :</i><br/>\n"+
		      rq+"<br/>\n"+e+"<br/>\n"+
		      e.getSQLException()+"<br/>\n");
      } catch (HibernateException e) {
	output.append("<i>Problème Hibernate : "+e+"</i><br/><br/>");
	tx.rollback();
      }
    } else {
      output.append("<i> ah soit vous n'êtes pas sûr ("+sure+"), "+
		    "soit vous voullez rien supprimer :)</i>"+
		    "<br/><br/>\n");
    }
  }
}
