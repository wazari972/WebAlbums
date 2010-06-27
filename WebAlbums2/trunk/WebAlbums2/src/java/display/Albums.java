package display;

import java.io.IOException;

import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Query ;
import org.hibernate.JDBCException;

import util.StringUtil;
import util.XmlBuilder;

import engine.WebPage.Mode;
import engine.WebPage.Type;
import entity.Album;
import entity.Photo;

import constante.Path ;
import engine.* ;
@SuppressWarnings("unchecked")
public class Albums extends HttpServlet {
  private static final long serialVersionUID = 1L;
	
  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    engine.Index.treat(WebPage.Page.ALBUM, request, response) ;
  }
  public void doPost(HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }
  
 
  public static XmlBuilder treatAlbmEDIT(HttpServletRequest request,
					 XmlBuilder submit)
    throws HibernateException {

    XmlBuilder output = new XmlBuilder("albm_edit") ;

    if (submit != null) output.add(submit);
    
    String albumID = request.getParameter("id") ;
    String page = request.getParameter("page") ;
    String count = request.getParameter("count") ;
    page = (page == null ? "0" : page) ;
    
    String rq = null ;
    try {
      rq = "from Album where id = '"+albumID.replace("'", "''")+"'" ;
      Album enrAlbum = (Album) WebPage.session.createQuery(rq).uniqueResult();
      rq = "done" ;

      if (enrAlbum == null) {
	output.cancel() ;
	output.addException("Impossible de trouver l'album ("+albumID+")");
	return output.validate ();
      }

      output.add("picture", enrAlbum.getPicture()) ;
      output.add("name", enrAlbum.getNom()) ;
      output.add("count", count) ;
      output.add("id", enrAlbum.getID()) ;
      output.add("description", enrAlbum.getDescription()) ;
      output.add("date", enrAlbum.getDate()) ;
      
      output.add(WebPage.displayListLB(Mode.TAG_USED, request, null,
				       WebPage.Box.MULTIPLE));
      output.add(WebPage.displayListLB(Mode.TAG_NUSED, request, null,
				       WebPage.Box.MULTIPLE));
      output.add(WebPage.displayListLB(Mode.TAG_NEVER, request, null,
				       WebPage.Box.MULTIPLE));
      output.add(WebPage.displayListDroit(enrAlbum.getDroit(), null));
      
      output.validate() ;
    } catch (JDBCException e) {
      output.cancel() ;
      output.addException("JDBCException", rq);
      output.addException("JDBCException", e.getSQLException());
    }
    return output.validate() ;
  }

  public static XmlBuilder displayAlbum(Query query,
					XmlBuilder output,
					HttpServletRequest request,
					XmlBuilder submit,
					XmlBuilder thisPage)
    throws HibernateException {
    
    WebPage.EditMode inEditionMode = WebPage.getEditionMode(request) ;
    String albumID = request.getParameter("id") ;
    String page = request.getParameter("page") ;
    String countAlbm = StringUtil.escapeURL(request.getParameter("count")) ;
    page = (page == null ? "0" : page) ;
    String strQuery = query.getQueryString() ;    
    long size = WebPage.session.createQuery(strQuery).list().size();
        
    Integer[] bornes =
      WebPage.calculBornes(Type.ALBUM, page, countAlbm, (int) size) ;

    query.setFirstResult(bornes[0]) ;
    query.setMaxResults(WebPage.TAILLE_ALBUM ) ;
    query.setReadOnly(true).setCacheable(true);
  
    int id ;
    try {
      id = Integer.parseInt(albumID) ;
    } catch (NumberFormatException e) {
      id = -1 ;
    }
    
    Album enrAlbum;

    String oldDate = null ;
    int count = bornes[0] ;

    Iterator it = query.iterate();
    while (it.hasNext()) {
      XmlBuilder album = new XmlBuilder ("album") ;
      enrAlbum = (Album) it.next();

      if (enrAlbum.getID() == id) {
	album.add(submit) ;
	submit = null ;
      }

      album.add(StringUtil.xmlDate(enrAlbum.getDate(), oldDate)) ;
      
      album.add("id",enrAlbum.getID()) ;
      album.add("count",count) ;
      album.add("title",enrAlbum.getNom()) ;

      XmlBuilder details = new XmlBuilder("details");
      Photo enrPhoto = enrAlbum.getPictureEnt() ;
      if (enrPhoto != null) {
	details.add("photoID",enrPhoto.getID()) ;
	details.add("miniWidth", enrPhoto.getWidth());
	details.add("miniHeight", enrPhoto.getHeight());
      }
      details.add("description",enrAlbum.getDescription()) ;
      
      //tags de l'album
      details.add(WebPage.displayListIBT(Mode.TAG_USED, request, enrAlbum.getID(),
					 WebPage.Box.NONE, Type.ALBUM));
      //utilisateur ayant le droit à l'album
      //ou a l'une des photos qu'il contient
      if (engine.Users.isLoggedAsCurrentManager(request)
	  && !Path.isReadOnly()) {
	if (inEditionMode != WebPage.EditMode.VISITE) {
	  details.add(WebPage.getUserName(enrAlbum.getDroit())) ;
	  details.add(WebPage.getUserInside(enrAlbum.getID())) ;
	}
      }
      album.add(details);

      if (WebPage.getMaps(request)) {
	//WebPage.displayMapId(request, output, enrAlbum.getID());
      }
      
      oldDate = enrAlbum.getDate();
      count++;
      
      output.add(album);
    }
    if (submit != null) output.add(submit);
    
    output.add(WebPage.xmlPage(thisPage, bornes));
    
    return output.validate() ;
  }
}