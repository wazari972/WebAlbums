package engine;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import constante.Path;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Transaction;
import org.hibernate.Query ;

import system.FilesFinder;
import util.StringUtil;
import util.XmlBuilder;

import entity.Album;

public class Albums {
  private static final long serialVersionUID = 1L;
  private static final int TOP = 5 ;
  @SuppressWarnings("unchecked")
public static XmlBuilder treatALBM(HttpServletRequest request)
    throws HibernateException {

    XmlBuilder output = new XmlBuilder("albums") ;
    XmlBuilder submit = null ;

    String special = request.getParameter("special") ;
    if ("TOP5".equals(special)) {
      XmlBuilder top5 = new XmlBuilder("top5") ;

      String rq = "FROM Album a " +
	" WHERE "+WebPage.restrictToAlbumsAllowed(request, "a")+" " +
	" AND "+WebPage.restrictToThemeAllowed(request, "a")+" " +
	" ORDER BY a.Date DESC " ;

      Query query = engine.WebPage.session.createQuery(rq);
      query.setMaxResults(TOP);
      int i = 0 ;
      Iterator it = query.iterate() ;
      while (it.hasNext()) {
	Album enrAlbum = (Album) it.next() ;
	XmlBuilder album = new XmlBuilder("album");
	album.add("id", enrAlbum.getID()) ;
	album.add("count", i) ;
	album.add("nom", enrAlbum.getNom()) ;
	if (enrAlbum.getPicture() != null) {
	  album.add("photo", enrAlbum.getPicture()) ;
	}
	top5.add(album);
      }
      output.add(top5) ;
      return output.validate() ;
    }

    
    String action = request.getParameter("action") ;    
    if ("SUBMIT".equals(action)
	&& Users.isLoggedAsCurrentManager(request)
	&& !Path.isReadOnly()) {
      submit = treatAlbmSUBMIT(request) ;
    }
    
    if ("EDIT".equals(action)
	&& Users.isLoggedAsCurrentManager(request)
	&& !Path.isReadOnly()) {
      output = display.Albums.treatAlbmEDIT(request, submit);
      
    } else {
      //sinon afficher la liste des albums de ce theme
      output.add(treatAlbmDISPLAY(request, submit));
    }
    
    return output.validate() ;
  }
  
  public static XmlBuilder treatAlbmSUBMIT(HttpServletRequest request)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder(null);
    String albumID = StringUtil.escapeHTML(request.getParameter("id")) ;
    String rq = null;
    Transaction tx = null ;
    try {
      rq = "FROM Album a "+
	" WHERE a.ID = '"+albumID+"' "+
	"AND "+WebPage.restrictToAlbumsAllowed(request, "a")+" " ;

      Album enrAlbum = (Album) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;

      if (enrAlbum == null) {
    	  return null ;
      }
      
      String supprParam = request.getParameter("suppr") ;
      if ("Oui je veux supprimer cet album".equals(supprParam)) {
	XmlBuilder suppr = new XmlBuilder("suppr_msg");
	if (FilesFinder.deleteAlbum (albumID, suppr)) {
	  output.add(suppr);
	  output.add("message", "Album correctement  supprimé !") ;
	} else {
	  output.addException(suppr);
	  output.addException("Exception", "an error occured ...");
	}
	
	 
	return output.validate() ;
      }

      String user = request.getParameter("user") ;
      String desc = StringUtil.escapeHTML(request.getParameter("desc")) ;
      String nom = StringUtil.escapeHTML(request.getParameter("nom")) ;
      String date = request.getParameter("date") ;
      String[] tags = request.getParameterValues("tags") ;
      String force = request.getParameter("force") ;

      tx = WebPage.session.beginTransaction();

      enrAlbum.updateDroit(new Integer("0"+user)) ;
      enrAlbum.setTagsToPhoto(tags, "yes".equals(force)) ;
      enrAlbum.setNom(nom) ;
      enrAlbum.setDescription(desc) ;
      enrAlbum.setDateStr(date) ;
      tx.commit();
      output.add("message", "Album ("+enrAlbum.getID()+") correctement mise à jour !") ;
      
    } catch (JDBCException e) {
      e.printStackTrace() ;
      output.cancel() ;
      output.addException("JDBCException", rq);
      output.addException("JDBCException", e.getSQLException()) ;

      if (tx != null) tx.rollback() ;
    } catch (WebPage.AccessorsException e) {
      e.printStackTrace() ;
      output.cancel() ;
      
      output.addException("AccessorsException", "Problème dans l'acces aux champs ...");
      if (tx != null) tx.rollback() ;
    }
    return output.validate() ;
  }

  public static XmlBuilder treatAlbmDISPLAY(HttpServletRequest request,
					    XmlBuilder submit)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder(null) ;
    String rq = null ;
    XmlBuilder thisPage = new XmlBuilder("name", "Albums");
    try {
      rq = "FROM Album a "+
	"WHERE "+WebPage.restrictToAlbumsAllowed(request, "a")+" " +
	"AND "+WebPage.restrictToThemeAllowed(request, "a")+" " +
	"ORDER BY a.Date DESC " ;
      
      Query query = engine.WebPage.session.createQuery(rq);
      query.setReadOnly(true).setCacheable(true) ;
      rq = "done" ;
            
      display.Albums.displayAlbum(query, output, request, submit, thisPage);      
    } catch (JDBCException e) {
      e.printStackTrace() ;
      output.cancel() ;
      
      output.addException("JDBCException", rq);
      output.addException("JDBCException", e.getSQLException()) ;
    }
    return output.validate() ;
  }
}
