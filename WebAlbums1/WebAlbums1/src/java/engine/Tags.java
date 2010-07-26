package engine;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import constante.Path;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Query;

import util.StringUtil;
import engine.WebPage.Mode;

import display.Photos ;

public class Tags {
  private static final long serialVersionUID = 1L;
    
  public static void treatTAGS(HttpServletRequest request,
				  StringBuilder output)
    throws WebPage.AccessorsException, HibernateException {
    String type = StringUtil.escapeHTML(request.getParameter("type")) ;
    String[] tags = request.getParameterValues("tagAsked");
    String tagList = "" ;
    String page = request.getParameter("page") ;

    String action = request.getParameter("action") ;
    
    if ("SUBMIT".equals(action)
	&& WebPage.isLoggedAsCurrentManager(request, output)
	&& !WebPage.isRootSession(request)) {
      action = engine.Photos.treatPhotoSUBMIT(request, output);
      //ensuite afficher la liste *normal* des photos
      //s'il n'y a pas eu de probleme
    }

    if ("EDIT".equals(action)
	&& WebPage.isLoggedAsCurrentManager(request)
	&& WebPage.isRootSession(request)) {
      Photos.treatPhotoEDIT(request, output);
      return ;
    }		
		
    
    //memoriser les params de lURL pour pouvoir revenir
    String from = Path.LOCATION+"Tags?" ;
    if (type != null) from += "&type="+type ;
    if (tags != null) tagList = addEach("tagAsked",tags) ;
    from += tagList ;
    
    String pageGet = from ;
    if (page != null) from += "&page="+page ;
    
    String rq = null ;
    try {
     
      if (tags != null) {
	List<Integer> listTagId = new ArrayList<Integer> (tags.length) ;
	for (int i = 0; i < tags.length; i++) {
	  try {
	    listTagId.add(Integer.parseInt(tags[i]));
	  } catch (NumberFormatException e) {}
	}
	output.append(WebPage.getHeadBand(request));
	output.append("<b>Photos sur les thèmes :</b><br /> \n"+
		      "<i>\n");
	WebPage.displayListLB(Mode.TAG_USED, request, output, listTagId,
			      WebPage.Box.NONE) ;
	output.append("</i><br/><br/>\n");
	
	
	//creation de la requete
	rq = "SELECT p FROM Photo p, Album a, TagPhoto tp " +
	  "WHERE a.ID = p.Album and p.ID = tp.Photo"+
	  " AND tp.Tag in ('-1' " ;
	for (int id : listTagId) {
	  rq += ", '"+id+"'" ;
	}
	rq += ")" ;
	
	if (!WebPage.isLoggedAsCurrentManager(request)) {
	  rq += "AND p.ID in ("+WebPage.listPhotoAllowed(request)+") " ;
	}
	if (!WebPage.isRootSession(request)) {
	  rq += "AND a.Theme = '"+WebPage.getThemeID(request)+"' " ;
	}
	//rq += "order by album.Date desc " ;
	//rq += "group by photo.ID" ;
	
	Query query = WebPage.session.createQuery(rq);
	query.setReadOnly(true).setCacheable(true);
	rq = "done" ;
	
	display.Photos.displayPhoto(query, output, request, null,
				    pageGet, StringUtil.escapeURL(tagList)) ;
      }
      output.append("<a href='"+Path.LOCATION+"Choix'>"+
		    "Retour aux choix</a>\n");
    }catch (JDBCException e) {
      output.append("<i> Impossible d'effectuer la requete' :</i>"+
		    rq+"<br/>"+e+"<br/>\n"+e.getSQLException()+"<br/>\n");
    }
  }
  
  private static String addEach(String name, String[] from) {
    String dest = "" ;
    for (int i = 0; i < from.length; i++) {
      dest += "&"+name+"="+StringUtil.escapeHTML(from[i]) ;
    }
    return dest ;
  }
  
}
