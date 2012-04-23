package display;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Query;

import util.StringUtil;
import util.XmlBuilder;

import engine.WebPage ;

public class Periode extends HttpServlet {
  private static final long serialVersionUID = 1L;
	
  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    engine.Index.treat(WebPage.Page.PERIODE, request, response) ;
  }
  public void doPost(HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }
  
  public static XmlBuilder treatPERIODE(HttpServletRequest request)
    throws HibernateException {
	  XmlBuilder output = new XmlBuilder("periode") ;
    String year = StringUtil.escapeHTML(request.getParameter("year")) ;
    String month = StringUtil.escapeHTML(request.getParameter("month")) ;
    String debut = StringUtil.escapeHTML(request.getParameter("debut")) ;
    String fin = StringUtil.escapeHTML(request.getParameter("fin")) ;
    String page = request.getParameter("page") ;
    
    //memoriser les params de lURL pour pouvoir revenir
    String from = "Periode?" ;
    if (year != null) from += "&year="+year ;
    if (month != null) from += "&month="+month ;
    if (debut != null) from += "&debut="+debut ;
    if (fin != null) from += "&fin="+fin ;
    if (page != null) from += "&page="+page ;
    request.getSession().setAttribute("from", from) ;
    
    String rq = null ;
    try {
      String desc ;
      if (debut == null) {
	if (year == null) {
	  throw new NumberFormatException(
	    "Infos Periode non consistantes (no year, no begin)") ;
	}
	try {
	  //essayer de trouver le mois
	  int mois = Integer.parseInt(month)+1 ;
	  
	  //si ça passe, c'est qu'il a été renseigné
	  debut = year+"-"+month+"-01" ;
	  
	  if (mois > 13) {
	    //mais il peut quand meme etre incorrect
	    throw new NumberFormatException (
	      "Mois supérieur à décembre ("+(mois-1)+")") ;
	  } else if (mois == 13) {
	    month = "1" ;
	    year = Integer.toString(Integer.parseInt(year)+1) ;
	  } else {
	    month = Integer.toString(mois);
					}
	  fin = year+"-"+month+"-01" ;
	  
	  desc = "de " + new SimpleDateFormat("MMMM yyyy")
	    .format(WebPage.DATE_STANDARD.parse(debut)) ;
	} catch (NumberFormatException e) {
	  //on ne peut pas trouver le mois, on fait comme s'il n'existait pas
	  
	  debut = year+"-01-01" ;
	  fin = Integer.toString(Integer.parseInt(year)+1)+"-01-01" ;
	  desc = "de l'année " + year ;
	}
      } else {
	if (fin == null) {
	  throw new NumberFormatException("Pas de date de fin de periode") ;
				}
	desc = "entre le " + debut + " et le "+fin ;
	//convertir from FRANCE to STANDARD
	debut = WebPage.DATE_STANDARD.format(WebPage.DATE_FRANCE.parse(debut)) ;
	fin = WebPage.DATE_STANDARD.format(WebPage.DATE_FRANCE.parse(fin)) ;
      }
      if (debut.compareTo(fin) > 0) {
	throw new NumberFormatException(
	  "Periode negative ! (from "+debut+" to "+fin+")") ;
      }
      
      output.add("descr", desc);
      
      rq = "FROM Album a " +
	"WHERE a.Date BETWEEN '"+debut+"' AND '"+fin+"' " +
	"AND "+WebPage.restrictToAlbumsAllowed(request, "a")+" " +
	"AND "+WebPage.restrictToThemeAllowed(request, "a")+" " +
	"ORDER BY Date ";
      
      Query query = WebPage.session.createQuery(rq);
      rq = "done" ;
      
      Albums.displayAlbum(query, output, request, null, new XmlBuilder(null));		
    } catch (NumberFormatException e) {
      e.printStackTrace() ;
      output.addException("NumberFormatException","Impossible de comprendre ces dates !") ;
    } catch (ParseException e) {
      e.printStackTrace() ;
      output.addException("ParseException", "Format de date incorrect... ") ;
    } catch (JDBCException e) {
    	e.printStackTrace() ;
    	
      output.addException("JDBCException", rq) ;
      output.addException("JDBCException", e.getSQLException());
    }
    return output.validate();
  }  
}
