package display;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List ;
import java.util.ArrayList ;
import java.util.Collections ;
import java.util.Set ;
import java.util.HashSet ;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import engine.WebPage ;
import engine.WebPage.AccessorsException;
import engine.WebPage.Mode;
import engine.WebPage.Type;
import entity.Photo;
import entity.Album;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.Transaction;

import util.StringUtil;
import util.XmlBuilder;

public class Photos extends HttpServlet {
  private static final long serialVersionUID = 1L;
	
  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    engine.Index.treat(WebPage.Page.PHOTO, request, response) ;
  }
  public void doPost(HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }
  

  public static XmlBuilder treatPhotoEDIT(HttpServletRequest request, XmlBuilder submit)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder("photo_edit") ;
    if (submit != null) output.add(submit) ;
    
    String photoID = StringUtil.escapeHTML(request.getParameter("id")) ;
    String page = StringUtil.escapeHTML(request.getParameter("page")) ;
    page = (page == null ? "0" : page);
    String rq = null ;
    try {
      rq = "SELECT a, p FROM Photo p, Album a WHERE a.ID = p.Album AND p.ID = '"+photoID+"'" ;
      Object[] result = (Object[]) WebPage.session.createQuery(rq).uniqueResult() ;
      rq = "done" ;
      Photo enrPhoto = (Photo) result[1] ;
      Album enrAlbum = (Album) result[0] ;
      if (enrPhoto == null) {
	output.addException("Impossible de trouver la photo ("+photoID+")");
	return output.validate();
      }
      output.add("id", enrPhoto.getID());
      output.add("description", enrPhoto.getDescription());
      output.add(WebPage.displayListIBT(Mode.TAG_USED, request, enrPhoto.getID(),
					WebPage.Box.MULTIPLE, Type.PHOTO)) ;
      
      output.add(WebPage.displayListIBT(Mode.TAG_NUSED, request, enrPhoto.getID(),
					WebPage.Box.MULTIPLE, Type.PHOTO)) ;
      
      output.add(WebPage.displayListIBT(Mode.TAG_NEVER, request, enrPhoto.getID(),
					WebPage.Box.MULTIPLE, Type.PHOTO)) ;
      
      output.add(WebPage.displayListIBTNI(Mode.TAG_USED, request, enrPhoto.getID(),
					  WebPage.Box.LIST, Type.PHOTO,
					  null, null));
      output.add(WebPage.displayListDroit(enrPhoto.getDroit(), enrAlbum.getDroit()));
      output.validate() ;
    } catch (JDBCException e) {
      e.printStackTrace() ;
      output.cancel();
      output.addException("JDBCException", rq);
      output.addException("JDBCException", e.getSQLException());
    }
    return output.validate() ;
  }

  @SuppressWarnings("unchecked")
  public static XmlBuilder displayPhoto(Query query,
					HttpServletRequest request,
					XmlBuilder thisPage,
					String albmCount,
					XmlBuilder submit)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder(null) ;
    
    WebPage.EditMode inEditionMode = WebPage.getEditionMode(request) ;
    String page = request.getParameter("page") ;
    String photoID = request.getParameter("id") ;
    String scount = request.getParameter("count") ;
    page = (page == null ? "0" : page) ;
    
    String strQuery = query.getQueryString() ;
    long size = WebPage.session.createQuery(strQuery).list().size();
    
    Integer[] bornes = WebPage.calculBornes(Type.PHOTO, page,
					    scount, (int) size) ;
    query.setReadOnly(true).setCacheable(true);
    query.setFirstResult(bornes[0]) ;
    query.setMaxResults(WebPage.TAILLE_PHOTO) ;
    
    String degrees = "0" ;
    String tag = null ;
    String rq = null ;
    int countME = 0 ;
    boolean massEditParam = false ;
    boolean reSelect = false ;
    boolean current = false ;

    String turn = request.getParameter("turn") ;
    if (inEditionMode == WebPage.EditMode.EDITION) {
      try {
	String action = request.getParameter("action") ;
	if ("MASSEDIT".equals(action)) {
	  if ("gauche".equals(turn)) {
	    degrees = "270" ;
	  } else if ("droite".equals(turn)) {
	    degrees = "90" ;
	  } else if ("tag".equals(turn) || "untag".equals(turn) || "movtag".equals(turn)) {
	    tag = StringUtil.escapeHTML(request.getParameter("addTag")) ;
	  }
	  
	  massEditParam = true ;
	}
      } catch (JDBCException e) {
	output.addException("JDBCException", rq);
	output.addException("JDBCException", e.getSQLException());
      } catch (NoSuchElementException e) {
	output.addException("NoSuchElementException", tag);
	reSelect = true ;
      }
    }
    
    Photo enrPhoto = null;
    int count = bornes[0] ;
    
    Iterator it = query.iterate() ;
    while (it.hasNext()) {
      enrPhoto = (Photo) it.next();
      XmlBuilder photo = new XmlBuilder("photo") ;
      boolean reSelectThis = false ;
      if (massEditParam) {
	String chkbox = request.getParameter("chk"+enrPhoto.getID()) ;
	if ("modif".equals(chkbox)) {
	  current = true ;
	  Transaction tx = null ;
	  try {
	    if ("tag".equals(turn) || "untag".equals(turn) || "movtag".equals(turn)) {
	      String verb ;
	      tx = WebPage.session.beginTransaction() ;
	      if ("tag".equals(turn)) {
		enrPhoto.addTags(new String[] {tag});
		verb = "added" ;
	      } else if ("untag".equals(turn)) {
		enrPhoto.removeTag(tag) ;
		verb = "removed" ;
	      } else if ("movtag".equals(turn)) {
		String rmTag = StringUtil.escapeHTML(request.getParameter("rmTag")) ;
		enrPhoto.removeTag(rmTag) ;
		enrPhoto.addTags(new String[] {tag});
		verb = "added and tag "+rmTag+" removed" ;
	      } else {
		verb = "nothinged" ;
	      }
	      WebPage.session.update(enrPhoto) ;
	      tx.commit() ;
	      photo.add("message", "Tag "+tag+" "+verb+" to photo #"+enrPhoto.getID());
	    } else if ("droite".equals(turn) || "gauche".equals(turn)) {
	      if (!enrPhoto.rotate(degrees)) {
		photo.addException("Erreur dans le ConvertWrapper ...");
		reSelectThis = true ;
	      }
	    }
	    
	    countME++ ;
	  } catch (AccessorsException e) {
	    photo.addException("AccessorsException", "Impossible d'effectuer l'action sur cette photo..."+e) ;
	    reSelectThis = true ;
	    if (tx != null) tx.rollback();
	  }
	}
      }

      if (enrPhoto.getID().equals(photoID)) {
	if (submit != null) {
	  photo.add(submit) ;
	  submit = null ;
	}
      }	
      if (inEditionMode == WebPage.EditMode.EDITION) {
	if ((reSelect || reSelectThis) && current)
	  photo.add("checked");
      }
      XmlBuilder details = new XmlBuilder("details");
      details.add("photoID", enrPhoto.getID());
      details.add("description", enrPhoto.getDescription());
      
      details.add("miniWidth", enrPhoto.getWidth(false));
      details.add("miniHeight", enrPhoto.getHeight(false));

      //tags de cette photo
      details.add(WebPage.displayListIBT(Mode.TAG_USED, request, enrPhoto.getID(),
					 WebPage.Box.NONE, Type.PHOTO));
      details.add("albumID", enrPhoto.getAlbum());
      //liste des utilisateurs pouvant voir cette photo
      if (engine.Users.isLoggedAsCurrentManager(request) &&
	  inEditionMode != WebPage.EditMode.VISITE)
      {
	Integer right = enrPhoto.getDroit() ;
	if (right != null && !right.equals(0)) {
	  details.add(WebPage.getUserName(enrPhoto.getDroit())) ;
	} else {
	  details.add(WebPage.getUserOutside(enrPhoto.getAlbum())) ;
	}
      }
      photo.add(details);
      photo.add("count", count);
      if (WebPage.getDetails (request)) {
	photo.add(enrPhoto.getXmlExif()) ;
      }
      output.add(photo);
      current = false ;
      count++ ; 
    }

    if (submit != null) {
      output.add(submit) ;
    }
    
    if (inEditionMode == WebPage.EditMode.EDITION) {
      XmlBuilder massEdit = new XmlBuilder("massEdit");
      massEdit.add(WebPage.displayListBN(WebPage.Mode.TAG_USED, request,
				       WebPage.Box.LIST, "newTag")) ;
      if (massEditParam) {
	String msg ; 
	if (countME == 0 || "rien".equals(turn)) {
	  msg = "Aucune modification faite ... ?";
	} else { 
	  msg = ""+ countME+" photo"+
	    (countME == 1 ? " a été modifiée" :
	     "s ont été modifées");
	}
	massEdit.add("message", msg);
      } 
      output.add(massEdit);
    }
    output.add(WebPage.xmlPage(thisPage, bornes));
    return output.validate() ;
  }
}