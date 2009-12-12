package engine;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import constante.Path;

import engine.WebPage ;
import entity.Album;
import entity.Photo;
import entity.TagTheme;
import entity.Tag;

import system.SystemTools ;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Query;

import system.FilesFinder;
import util.StringUtil;
import util.XmlBuilder;

import org.hibernate.Transaction;

public class Photos {
  private static final long serialVersionUID = 1L;
    
  public static XmlBuilder treatPHOTO(HttpServletRequest request)
    throws WebPage.AccessorsException, HibernateException {
    String action = request.getParameter("action") ;
    XmlBuilder output ;
    XmlBuilder submit = null ;
    Boolean correct = new Boolean (true);

    if ("SUBMIT".equals(action)
	&& Users.isLoggedAsCurrentManager(request)
	&& !Path.isReadOnly()) {
      submit = treatPhotoSUBMIT(request, correct);
    }
    
    if (("EDIT".equals(action) || !correct)
	&& Users.isLoggedAsCurrentManager(request)
	&& !Path.isReadOnly()) {
      output = display.Photos.treatPhotoEDIT(request, submit);
      
      XmlBuilder return_to = new XmlBuilder("return_to");
      return_to.add("name", "Photos");
      return_to.add("count", request.getParameter("count"));
      return_to.add("album", request.getParameter("album"));
      return_to.add("albmCount", request.getParameter("albmCount"));
      output.add(return_to);
    } else {
      output = new XmlBuilder ("photos");
      output.add(treatPhotoDISPLAY(request, submit));
    }
    
    
    return output.validate() ;
  }
	
  @SuppressWarnings("unchecked")
  protected static XmlBuilder treatPhotoSUBMIT(HttpServletRequest request,
					       Boolean correct)
    throws WebPage.AccessorsException, HibernateException {
    XmlBuilder output = new XmlBuilder(null) ;
    String photoID = StringUtil.escapeHTML(request.getParameter("id")) ;
    String theme = WebPage.getThemeID(request) ;
    String rq = null ;
    Transaction tx = null ;
    try {
      //rechercher la photo
      rq = "select ph from Album al, Photo ph" +
	" where al.ID = ph.Album " +
	" and ph.ID = '"+photoID+"'" +
	(WebPage.isRootSession(request) ? "" : " and al.Theme = '"+theme+"' ") ;

      Photo enrPhoto = (Photo) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;
      
      if (enrPhoto == null) {
	correct = false ;
	output.addException("Impossible de trouver cette photo "+
			    "("+photoID+ (WebPage.isRootSession(request) ? "" : "/"+theme) +")");
	return output.validate() ;
      }

      //supprimer ?
      String suppr = request.getParameter("suppr") ;
      if ("Oui je veux supprimer cette photo".equals(suppr)) {
	XmlBuilder suppr_msg = new XmlBuilder("suppr_msg");
	FilesFinder.deletePhoto (photoID, suppr_msg) ; 
	output.add(suppr_msg);
	return output.validate() ;
      }

      //mise à jour des tag/description
      String user = request.getParameter("user") ;
      String desc = StringUtil.escapeHTML(request.getParameter("desc")) ;
      String[] tags = request.getParameterValues("newTag");
      
      tx = WebPage.session.beginTransaction() ;

      enrPhoto.updateDroit("null".equals(user) ? null : new Integer("0"+user)) ;
      enrPhoto.setTags(tags) ;
      enrPhoto.setDescription(desc) ;

      WebPage.session.update(enrPhoto);
      tx.commit();
      
      //utiliser cette photo comme representante de l'album ?
      String represent = request.getParameter("represent") ;
      if ("y".equals(represent)) {
	
	rq = "from Album where id = '"+enrPhoto.getAlbum()+"'" ;
	Album enrAlbum = (Album)  WebPage.session.createQuery(rq).uniqueResult();
	rq = "done" ;
	
	if (enrAlbum == null) {
	  output.addException("Exception", "Impossible d'acceder l'album à representer "+
			      "("+enrPhoto.getAlbum()+")") ;
	  return output.validate() ;
	} 
	tx = WebPage.session.beginTransaction() ;
	
	enrAlbum.setPicture(enrPhoto.getID());
	WebPage.session.update(enrAlbum);
	
	tx.commit();
      }
      
      //utiliser cette photo pour representer le tag de ce theme
      String tagPhoto = StringUtil.escapeHTML(request.getParameter("tagPhoto")) ;
      if (tagPhoto != null && !"-1".equals(tagPhoto)) {
	rq = "from TagTheme "+
	  "where tag = '" + tagPhoto + "'" +
	  (WebPage.isRootSession(request) ? "" : " and theme = "+WebPage.getThemeID(request)) ;

	String actualTheme ;
	TagTheme enrTagTh = null ;
	if (!WebPage.isRootSession(request)) {
	  actualTheme = WebPage.getThemeID(request) ;
	  
	  enrTagTh = (TagTheme)  WebPage.session.createQuery(rq).uniqueResult() ;
	  rq = "done" ;
	} else {
	  Iterator it = WebPage.session.createQuery(rq).iterate() ;

	  rq = "select a.Theme from Album a where a.ID = "+enrPhoto.getAlbum() ;
	  Integer itheme = (Integer) WebPage.session.createQuery(rq).uniqueResult() ;
	  rq = "done" ;
	  if (itheme == null) throw new NoSuchElementException("Album with ID="+enrPhoto.getAlbum());
	  actualTheme = itheme.toString();
	  
	  while(it.hasNext()) {
	    enrTagTh = (TagTheme) it.next() ;
	    WebPage.log.info("tag th"+enrTagTh.getTheme()+" ta"+enrTagTh.getTag()) ;
	    if (enrTagTh.getTheme() == itheme) {
	      break ;
	    }
	  }

	  if (enrTagTh.getTheme() != itheme) {
	    enrTagTh = null ;
	  }
	}
	  
	if (enrTagTh == null) {
	  rq = "from Tag where ID = '"+tagPhoto+"'" ;
	  Tag enrTag = (Tag)  WebPage.session.createQuery(rq).uniqueResult() ;
	  rq = "done" ;

	  //creer un tagTheme pour cette photo/tag/theme
	  enrTagTh = new TagTheme() ;

	  enrTagTh.setTheme (actualTheme) ;
	  enrTagTh.setTag (enrTag.getID()) ;
	  //par défaut le tag est visible
	  enrTagTh.setIsVisible(true) ;
	}
	//changer la photo representant ce tag/theme
	enrTagTh.setPhoto(enrPhoto.getID()) ;

	tx = WebPage.session.beginTransaction() ;
	WebPage.log.info("saveOrUpdate "+enrTagTh);
	WebPage.session.saveOrUpdate(enrTagTh);
	tx.commit();
      }
            
      output.add("message", " Photo ("+enrPhoto.getID()+") "+
		 "correctement mise à jour !");
    } catch (JDBCException e) {
      e.printStackTrace() ;

      output.cancel() ;
      output.addException("JDBCException", "Impossible d'effectuer la modification de la photo "+
			  "("+photoID+")") ;
      output.addException("JDBCException", rq) ;
      output.addException("JDBCException", e.getSQLException());

      if (tx != null) tx.rollback();
      correct = false ;
      
    } catch (NoSuchElementException e) {
      e.printStackTrace() ;

      output.cancel() ;
      output.addException("NoSuchElementException", "Impossible d'accerder à la photo à modifier ("+photoID+")") ;
      output.addException("NoSuchElementException", rq);
      
      if (tx != null) tx.rollback();
      correct = false ;
    }
    return output.validate() ;
  }	
	
  protected static XmlBuilder treatPhotoDISPLAY(HttpServletRequest request, XmlBuilder submit)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder(null);
    //afficher les photos
    //afficher la liste des albums de cet theme
    String albumID = StringUtil.escapeHTML(request.getParameter("album")) ;
    String page = StringUtil.escapeHTML(request.getParameter("page")) ;
    String albmCount = StringUtil.escapeURL(request.getParameter("albmCount")) ;
    String special = request.getParameter("special") ;
    page = (page == null ? "0" : page);
    
    Album enrAlbum = null ;
    String rq = null ;
    try {
      rq = "FROM Album a " +
	"WHERE a.ID = '"+albumID+"' " +
	"AND "+WebPage.restrictToAlbumsAllowed(request, "a")+" " +
	"AND "+WebPage.restrictToThemeAllowed(request, "a")+" " ;
      
      enrAlbum = (Album) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;
      
      if (enrAlbum == null) {
    	output.addException("L'album ("+albumID+") n'existe pas "+
			    "ou n'est pas accessible...") ;
	return output.validate();
      }
      XmlBuilder album = new XmlBuilder ("album") ;
      output.add(album) ;

      album.add("id",enrAlbum.getID()) ;
      album.add("count",albmCount) ;
      album.add("title",enrAlbum.getNom()) ;
      album.add(WebPage.getUserName(enrAlbum.getDroit())) ;
      album.add(StringUtil.xmlDate(enrAlbum.getDate(), null)) ;
      album.add(new XmlBuilder("details")
		.add("description",enrAlbum.getDescription())
		.add("photoID",enrAlbum.getPicture())) ;

      rq = "FROM Photo p " +
	" WHERE p.Album = '"+albumID+"' " +
      	" AND "+WebPage.restrictToPhotosAllowed(request, "p")+" " +
	" ORDER BY path" ;
      
      Query query = WebPage.session.createQuery(rq);
      query.setReadOnly(true).setCacheable(true) ;

      if ("FULLSCREEN".equals(special)) {
	SystemTools.fullscreen(query, "Albums", enrAlbum.getID().toString(), page, request);
      }
      
      XmlBuilder thisPage = new XmlBuilder(null);
      thisPage.add("name", "Photos");
      thisPage.add("album", albumID);
      thisPage.add("albmCount", albmCount) ;
      output.add(display.Photos.displayPhoto(query, request, thisPage, albmCount, submit));
      
    } catch (NullPointerException e) {
      e.printStackTrace() ;
      output.cancel() ;
      output.addException("NullPointerException","Quelque chose n'existe pas ... "+e) ;
    } catch (JDBCException e) {
      e.printStackTrace() ;
      output.cancel() ;
      output.addException("JDBCException", "Il y a une erreur dans la requete : "+rq);
      output.addException("JDBCException", e.getSQLException()) ;
    }
    return output.validate() ;
  }
}
