package engine ;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Transaction;

import util.FilesFinder;
import util.StringUtil;

import entity.*;

@SuppressWarnings("unchecked")
public class Config {
  private static final long serialVersionUID = -628341734743684910L;
  
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
      //List list  = session.createQuery(rq).list() ;
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
    Transaction tx = null ;
    try {
      rq = "from Tag where id = '"+tags+"'" ;
      Tag tag = (Tag) WebPage.session.createQuery(rq).uniqueResult() ;

      if (tag == null) {
	output.append("<i> Le Tag "+tags+
	      " n'est pas dans la base ...</i></br/>\n");
	return ;
      }
      
      rq = "from Tag where nom = '"+nouveau+"'" ;
      if (WebPage.session.createQuery(rq).uniqueResult() == null) {
	tx = WebPage.session.beginTransaction();
	tag.setNom (nouveau) ;
	WebPage.session.update(tag) ;
	
	output.append("<i> Tag correctement renomée en "+nouveau+"</i></br/>\n");
	tx.commit() ;
      } else {
	output.append("<i> Le Tag "+nouveau+
		      " est déjà présent dans la base ...</i></br/>\n");
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
      if (tx != null) tx.rollback() ;
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

    Transaction tx = null ;
    
    try {
      rq = "from Geolocalisation where tag = '"+tags+"'" ;
      Geolocalisation enrGeo =
	(Geolocalisation) WebPage.session.createQuery(rq).uniqueResult() ;
      rq = "done" ;

      if (enrGeo == null) {
	output.append("<i> La localisation "+tags+" ne correspond "+
		    "à aucun tag ... ? </i></br/>\n");
	return ;
      }
      
      tx = WebPage.session.beginTransaction();
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
      if (tx != null) tx.rollback() ;
    }
  }
  public static void treatMODVIS(HttpServletRequest request,
				  StringBuilder output)
    throws HibernateException {
    String rq = null ;

    String tags = StringUtil.escapeHTML(request.getParameter("tags")) ;
    String visible = request.getParameter("visible") ;

    if ("-1".equals(tags)) {
      output.append("<i> Pas de tag selectionné ...</i></br/>\n");
      return ;
    }

    Transaction tx = null ;
    
    try {
      rq = "from TagTheme where tag = '"+tags+
	"' and theme = '"+WebPage.getThemeID(request)+"'" ;
      TagTheme enrTagTheme = (TagTheme) WebPage.session.createQuery(rq).uniqueResult() ;
      rq = "done" ;

      tx = WebPage.session.beginTransaction();
      if (enrTagTheme == null) {
	rq = "from Tag where id = '"+tags+"'" ;
	if (WebPage.session.createQuery(rq).uniqueResult() == null) {
	  output.append("<i>Impossible de trouver ce tag ("+tags+") ...</i>\n");
	  tx.rollback() ;
	  return ;
	}
	//le tag existe
	rq = "done" ;
	
	enrTagTheme = new TagTheme () ;
	enrTagTheme.setTheme (WebPage.getThemeID(request)) ;
	enrTagTheme.setTag (tags) ;

	WebPage.session.save(enrTagTheme);
      }
      if ("y".equals(visible)) {
	enrTagTheme.setIsVisible (true) ;
      } else {
	enrTagTheme.setIsVisible (false) ;
      }
      WebPage.session.update(enrTagTheme);
      tx.commit() ;
      output.append("Le tag "+tags+" est maintenant : "+("y".equals(visible) ? "visible" :"invisible")+"<br/>\n");

    } catch (JDBCException e) {
      output.append("<i> Impossible d'effectuer la requete' :</i>"+
		    rq+"<br/>"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
      
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

    Transaction tx = null ;
    try {
      if (nom != null && !nom.equals("")) {
	String msg = "";
	String liste = "" ;
	
	rq = "from Tag where nom = '"+nom+"'" ;
	Tag enrTag  = (Tag) WebPage.session.createQuery(rq).uniqueResult() ;
	rq = "done" ;
	if (enrTag == null) {
	  int itype = Integer.parseInt (type) ;
	  if (0 > itype || itype > 3) {
	    output.append("<i> Type incorrect ("+type+") ...</i><br/><br/>\n");
	    return ;
	  }
	  tx = WebPage.session.beginTransaction();
	  enrTag = new Tag () ;
	  
	  enrTag.setNom(nom) ;
	  enrTag.setTagType(itype);
	  WebPage.session.save(enrTag) ;
	  output.append("<i> TAG ==> "+enrTag.getID()+" <==<br/>\n");
	  if (itype == 3) {
	    try {
	      String longit = StringUtil.escapeHTML(request.getParameter("long")) ;
	      String lat = StringUtil.escapeHTML(request.getParameter("lat")) ;
	      
	      Geolocalisation geo = new Geolocalisation () ;
	      geo.setTag(enrTag.getID ());
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
	    case 1 : liste = "Who" ; break ;
	    case 2 : liste = "What"  ; break ;
	    case 3 : liste = "Where" ; break ;
	  }
	  
	  output.append("<i>Tag</i> '"+nom+msg+"'"+
			"<i> correctement ajouté à la liste "+liste+
			"</i><br/><br/>\n");
	} else {
	  output.append("<i> Le Tag "+nom+
			" est déjà présent dans la base ...</i></br/>\n");
	  output.append("<i>"+enrTag.getID()+" - "+enrTag.getNom()+"</i><br/>\n");
	  
	  if (tx != null) tx.rollback() ;
	  return ;
	}
      } else {
	output.append("<i>Le nom du tag est vide ...</i><br/><br/>\n");
	if (tx != null) tx.rollback() ;
	return ;
      }
    } catch (JDBCException e) {
      output.append("<i> Impossible d'effectuer la requete' :</i>"+rq+
		    "<br/>"+e+"<br/>\n"+e.getSQLException()+"<br/>\n");
      
      if (tx != null) tx.rollback() ;
    } catch (NumberFormatException e) {
      output.append("<i>Erreur dans le cast de l'un des nombres : "+e
		    +"</i><br/><br/>");
      if (tx != null) tx.rollback() ;
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
	
	Iterator it = WebPage.session.createQuery(rq).iterate() ;
	rq = "done" ;
	int i = 0 ;
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	  i++ ;
	}
	output.append("<i> Suppression de "+i+" Tags Photo</i>"+
		      "<br/>\n");
	
	//liens Tag->Localisation
	rq = "from Geolocalisation where tag in ( '-1'" ;
	for (i = 0; i < tags.length; i++) {
	  rq += ", '"+tags[i]+"' " ;
	}
	rq += ")";
	it = WebPage.session.createQuery(rq).iterate() ;
	rq = "done" ;
	
	i = 0 ;
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	  i++ ;
	}
	output.append("<i> Suppression de "+i+" "+
		      "Geolocalisations</i><br/>\n");
	//tags
	rq = "from Tag where ID in ( '-1'" ;
	for (i = 0; i < tags.length; i++) {
	  rq += ", '"+tags[i]+"' " ;
	}
	rq += ")";
	it = WebPage.session.createQuery(rq).iterate() ;
	rq = "done" ;
	
	i = 0 ;
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	  i++ ;
	}
	output.append("<i> Suppression de "+i+" Tags</i><br/>\n");
	tx.commit();
	output.append("<br/>\n");
	return null ;
      } else {
	tx.rollback();
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
      tx.rollback();
      return null ;
    }
  }
  
  public static void treatNEWUSER(HttpServletRequest request,
				   StringBuilder output)
    throws HibernateException {
    Transaction tx = null ;
    String rq = null ;
    String nom = StringUtil.escapeHTML(request.getParameter("nom")) ;
    String pass = StringUtil.escapeHTML(request.getParameter("pass")) ;

    if (nom == null || nom.equals("")) {
      output.append("<i>Le nom de l'utilisateur est vide ...</i>"+
		    "<br/><br/>\n");
      return ;
    }
      
    rq = "from Theme where nom = '"+nom+"'" ;
    Theme enrTheme = (Theme) WebPage.session.createQuery(rq).uniqueResult() ;
    if (enrTheme != null) {
	output.append("<i>Le nom d'utilisateur est déjà utilisé  ...</i>"+
		      "<br/><br/>\n");
	return ;
    }

    rq = "from Utilisateur where nom = '"+nom+"'" ;
    Utilisateur enrUtil = (Utilisateur) WebPage.session.createQuery(rq).uniqueResult() ;
    if (enrUtil != null) {
      output.append("<i>Le nom d'utilisateur est déjà utilisé  ...</i>"+
		    "<br/><br/>\n");
      return ;
    }
    
    try {
      tx = WebPage.session.beginTransaction();
      
      enrUtil = new Utilisateur () ;
      enrUtil.setNom(nom) ;
      enrUtil.setPassword(("".equals(pass) ? null : pass)) ;
      WebPage.session.save(enrUtil) ;
      tx.commit();
      
      output.append("<i>Utilisateur </i>'"+nom+"'"+
		    "<i> correctement ajouté (<b>"+enrUtil.getID()+
		    "</b>)</i><br/><br/>\n");
      
    } catch (JDBCException e) {
      output.append("<i> Impossible d'effectuer la requete' :</i>"+
		    rq+"<br/>\n"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
      if (tx != null) tx.rollback();
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
	List list = WebPage.session.createQuery(rq).list() ;
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
	list = WebPage.session.createQuery(rq).list() ;
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
	list = WebPage.session.createQuery(rq).list() ;
	rq = "done" ;
	output.append("<i> Suppression de "+list.size()+" utilisateur"+
		      "</i><br/>\n");
	it = list.iterator();
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	}
	tx.commit();
	output.append("<br/>\n");
      } catch (JDBCException e) {
	tx.rollback();
	output.append("<i> Impossible d'effectuer la manip' :</i><br/>\n"+
		      rq+"<br/>\n"+e+"<br/>\n"+
		      e.getSQLException()+"<br/>\n");
      }
    } else {
      output.append("<i> ah soit vous n'êtes pas sûr ("+sure+"), "+
		    "soit vous voullez rien supprimer :)</i>"+
		    "<br/><br/>\n");
    }
  }
}
