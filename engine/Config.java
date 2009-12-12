package engine ;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Transaction;

import system.FilesFinder;
import util.StringUtil;
import util.XmlBuilder;

import entity.*;

@SuppressWarnings("unchecked")
public class Config {
  private static final long serialVersionUID = -628341734743684910L;
  
  public static XmlBuilder treatCONFIG(HttpServletRequest request)
    throws HibernateException {

    String special = request.getParameter("special") ;
    if (special != null) {
      WebPage.saveEditionMode(request);
      WebPage.saveDetails(request) ;
      WebPage.saveMaps(request) ;
      return new XmlBuilder("updated");
    }
    return display.Config.displayCONFIG(request) ;
  }

  public static XmlBuilder treatIMPORT(HttpServletRequest request)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder("import");
    String theme = StringUtil.escapeHTML(request.getParameter("importTheme")) ;
    String passwrd = StringUtil.escapeHTML(request.getParameter("passwrd")) ;
	
    try {
      output.add("message", "Begining ...") ;
      boolean correct = new FilesFinder ()
	.importAuthor(request, theme, passwrd, output);
      
      if (correct) output.add("message", "Well done !") ;
      else output.addException("An error occured ...") ;
    } catch (JDBCException e) {
      e.printStackTrace() ;
      output.cancel();
      output.addException("JDBCException", e.getSQLException());
    }
    return output.validate();
  }
  
  public static XmlBuilder treatMODTAG(HttpServletRequest request)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder("modTag");
    String rq = null ;
    
    String nouveau = StringUtil.escapeHTML(request.getParameter("nouveau")) ;
    String tag = StringUtil.escapeHTML(request.getParameter("tag")) ;

    if ("-1".equals(tag)) {
      output.addException("Pas de tag selectionné ...");
      return output.validate();
    }
    Transaction tx = null ;
    try {
      rq = "FROM Tag WHERE id = '"+tag+"'" ;
      Tag enrTag = (Tag) WebPage.session.createQuery(rq).uniqueResult() ;

      if (tag == null) {
	output.addException("Le Tag #"+tag+" n'est pas dans la base ...");
	return output.validate();
      }

      output.add("oldName", enrTag.getNom());
      tx = WebPage.session.beginTransaction();
      enrTag.setNom (nouveau) ;
      WebPage.session.update(enrTag) ;
      tx.commit() ;
      output.add("newName", nouveau);

    } catch (JDBCException e) {
      if (e.getSQLException().toString().contains("Duplicate")) {
	output.add("alreadyName", nouveau);
      } else {
	e.printStackTrace() ;
	output.cancel() ;
	output.addException("JDBCException", rq);
	output.addException("JDBCException", e.getSQLException());
      }
      new Exception().printStackTrace() ;
      if (tx != null) tx.rollback() ;
    } catch (NoSuchElementException e) {
      e.printStackTrace() ;
      output.cancel() ;
      output.addException("NoSuchElementException", e);
      new Exception().printStackTrace() ;
      if (tx != null) tx.rollback() ;
    }
    return output.validate() ;
  }

  public static XmlBuilder treatMODGEO(HttpServletRequest request)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder("modGeo");
    String rq = null ;
    
    String lng = StringUtil.escapeHTML(request.getParameter("lng")) ;
    String lat = StringUtil.escapeHTML(request.getParameter("lat")) ;
    String tag = StringUtil.escapeHTML(request.getParameter("tag")) ;

    if ("-1".equals(tag)) {
      output.addException("Pas de tag selectionné ...");
      return output.validate();
    }

    Transaction tx = null ;
    
    try {
      rq = "FROM Geolocalisation WHERE tag = '"+tag+"'" ;
      Geolocalisation enrGeo =
	(Geolocalisation) WebPage.session.createQuery(rq).uniqueResult() ;
      rq = "done" ;

      if (enrGeo == null) {
	output.addException("La localisation "+tag+" ne correspond à aucun tag ...");
	return output.validate() ;
      }

      if (lng == null || lat == null) {
	output.addException("La geoloc "+lng+"/"+lat+" n'est pas correcte...");
	
	if (tx != null) tx.rollback() ;
	return output.validate() ;
      }
      tx = WebPage.session.beginTransaction();
      enrGeo.setLong (lng) ;
      enrGeo.setLat(lat);
      WebPage.session.update(enrGeo) ;
      
      output.add("newLngLat", lng+"/"+lat);
      tx.commit() ;
    } catch (JDBCException e) {
      e.printStackTrace();
      output.cancel();
      output.addException("JDBCException", rq);
      output.addException("JDBCException", e.getSQLException());
      if (tx != null) tx.rollback() ;
    }
    return output.validate();
  }
  
  public static XmlBuilder treatMODVIS(HttpServletRequest request)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder("modVis");
    String rq = null ;

    if (WebPage.isRootSession(request)) {
      output.addException("impossible to change visibility on the root session");
      return output.validate();
    }
    
    String tag = StringUtil.escapeHTML(request.getParameter("tag")) ;
    String visible = request.getParameter("visible") ;

    if ("-1".equals(tag)) {
      output.addException("Pas de tag selectionné ...");
      return output.validate();
    }

    Transaction tx = null ;
    
    try {
      rq = "FROM TagTheme "+
	" WHERE tag = '"+tag+"' "+
	" AND theme = '"+WebPage.getThemeID(request)+"'" ;
      TagTheme enrTagTheme = (TagTheme) WebPage.session.createQuery(rq).uniqueResult() ;
      rq = "done" ;

      tx = WebPage.session.beginTransaction();
      if (enrTagTheme == null) {
	rq = "FROM Tag WHERE id = '"+tag+"'" ;
	if (WebPage.session.createQuery(rq).uniqueResult() == null) {
	  output.addException("Impossible de trouver ce tag ("+tag+") ...");
	  tx.rollback() ;
	  return output.validate();
	}
	//le tag existe
	rq = "done" ;
	
	enrTagTheme = new TagTheme () ;
	enrTagTheme.setTheme (WebPage.getThemeID(request)) ;
	enrTagTheme.setTag (tag) ;

	WebPage.session.save(enrTagTheme);
      }
      if ("y".equals(visible)) {
	enrTagTheme.setIsVisible (true) ;
      } else {
	enrTagTheme.setIsVisible (false) ;
      }
      WebPage.session.update(enrTagTheme);
      tx.commit() ;
      output.add("message", "Le tag "+tag+" est maintenant : "+("y".equals(visible) ? "visible" :"invisible"));

    } catch (JDBCException e) {
      e.printStackTrace();
      output.cancel();
      output.addException("JDBCException", rq);
      output.addException("JDBCException", e.getSQLException());
      tx.rollback() ;
    }
    return output.validate() ;
  }
  
  public static XmlBuilder treatNEWTAG(HttpServletRequest request)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder("newTag");
    String rq = null ;
    String nom = StringUtil.escapeHTML(request.getParameter("nom")) ;
    String type = StringUtil.escapeHTML(request.getParameter("type")) ;

    if ("-1".equals(type)) {
      output.addException("Pas de type selectionné ...");
      return output.validate();
    }

    Transaction tx = null ;
    try {
      if (nom != null && !nom.equals("")) {
	String msg = "";
	String liste = "" ;
	
	rq = "FROM Tag WHERE nom = '"+nom+"'" ;
	Tag enrTag  = (Tag) WebPage.session.createQuery(rq).uniqueResult() ;
	rq = "done" ;
	if (enrTag == null) {
	  int itype = Integer.parseInt (type) ;
	  if (0 > itype || itype > 3) {
	    output.addException("Type incorrect ("+type+") ...");
	    return output.validate();
	  }
	  tx = WebPage.session.beginTransaction();
	  enrTag = new Tag () ;
	  
	  enrTag.setNom(nom) ;
	  enrTag.setTagType(itype);
	  WebPage.session.save(enrTag) ;
	  output.add("message", "TAG == "+enrTag.getID()+" ==");
	  if (itype == 3) {
	    try {
	      String longit = StringUtil.escapeHTML(request.getParameter("long")) ;
	      String lat = StringUtil.escapeHTML(request.getParameter("lat")) ;
	      msg = " ("+longit+"/"+lat+")" ;
	      if (longit == null || lat == null) {
		output.cancel();
		output.addException("La geoloc "+msg+" n'est pas correcte...");
		WebPage.session.delete(enrTag) ;
		if (tx != null) tx.rollback() ;
		return output.validate() ;
	      }
	      
	      Geolocalisation geo = new Geolocalisation () ;
	      geo.setTag(enrTag.getID ());
	      geo.setLong(longit);
	      geo.setLat (lat);
	      
	      WebPage.session.save(geo);
	      
	    } catch (JDBCException e) {
	      e.printStackTrace() ;
	      output.cancel();
	      output.addException("JDBCException", rq);
	      output.addException("JDBCException", e.getSQLException());
	      tx.rollback();
	      return output.validate() ;
	    }
	  }
	  tx.commit();
	  
	  switch (Integer.parseInt(type)) {
	    case 1 : liste = "Who" ; break ;
	    case 2 : liste = "What"  ; break ;
	    case 3 : liste = "Where" ; break ;
	  }
	  
	  output.add("message", "Tag '"+nom+msg+"' correctement ajouté à la liste "+liste);
	} else {
	  output.cancel();
	  output.addException("Le Tag "+nom+" est déjà présent dans la base ...");
	  output.addException(enrTag.getID()+" - "+enrTag.getNom());
	  
	  if (tx != null) tx.rollback() ;
	  return output.validate() ;
	}
      } else {
	output.cancel() ;
	output.addException("Le nom du tag est vide ...");
	if (tx != null) tx.rollback() ;
	return output.validate() ;
      }
    } catch (JDBCException e) {
      e.printStackTrace() ;
      output.cancel() ;
      output.addException("JDBCException", rq);
      output.addException("JDBCException", e.getSQLException()) ;

      
      if (tx != null) tx.rollback() ;
    } catch (NumberFormatException e) {
      e.printStackTrace() ;
      output.cancel() ;
      output.addException("NumberFormatException", "Erreur dans le cast de l'un des nombres");
		 
      if (tx != null) tx.rollback() ;
    }
    return output.validate();
  }
  public static XmlBuilder treatDELTAG(HttpServletRequest request)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder("delTag");

    String rq = null ;
    String tag = StringUtil.escapeHTML(request.getParameter("tag"));
    
    String sure = request.getParameter("sure") ;

    Transaction tx = null ;
    try {
      if (tag != null && String.CASE_INSENSITIVE_ORDER.compare(sure, "yes") == 0) {
	
	tx = WebPage.session.beginTransaction();
	
	//liens Tag->Photos
	rq = "SELECT tp "+
	  " FROM TagPhoto tp, Tag t "+
	  " WHERE t.ID = '"+tag+"' " +
	  " AND t.ID = tp.Tag";
	
	Iterator it = WebPage.session.createQuery(rq).iterate() ;
	rq = "done" ;
	int i = 0 ;
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	  i++ ;
	}
	output.add("message", "Suppression de "+i+" Tags Photo");
	
	//liens Tag->Localisation
	rq = "FROM Geolocalisation WHERE tag = '"+tag+"'" ;
	it = WebPage.session.createQuery(rq).iterate() ;
	rq = "done" ;
	
	i = 0 ;
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	  i++ ;
	}
	output.add("message", "Suppression de "+i+" Geolocalisations");

	//liens Tag->Theme
	rq = "FROM TagTheme WHERE tag = '"+tag+"'" ;
	it = WebPage.session.createQuery(rq).iterate() ;
	rq = "done" ;
	
	i = 0 ;
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	  i++ ;
	}
	output.add("message", "Suppression de "+i+" TagThemes");

	//tag
	rq = "FROM Tag WHERE id = '"+tag+"'" ;
	it = WebPage.session.createQuery(rq).iterate() ;
	rq = "done" ;
	
	i = 0 ;
	while (it.hasNext()) {
	  WebPage.session.delete(it.next());
	  i++ ;
	}
	output.add("message", "Suppression de "+i+" Tags");

	tx.commit();

	return output.validate() ;
      } else {
	if (tag != null) {
	  output.addException("Vous n'êtes pas sûr ("+sure+") ?");
	  output.add("selected", tag) ;

	  return output.validate() ;
	} else {
	  output.addException("Aucun tag selectionné ...");
	  return output.validate() ;
	}
      }
    } catch (JDBCException e) {
      e.printStackTrace() ;
      
      if (tx != null) tx.rollback() ;
      output.cancel();
      output.addException("JDBCException", rq) ;
      output.addException("JDBCException", e.getSQLException());

      return output.validate() ;
    }
  }
}
