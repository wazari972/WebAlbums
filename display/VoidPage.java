package display ;

import engine.WebPage;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import entity.* ;
import java.util.Iterator ;
import java.util.List ;
import constante.Path ;

public class VoidPage {
 public static void treatVOID(StringBuilder output)
    throws HibernateException {
    //afficher la liste des themes
    String rq = null ; 
    try  {
      rq = "from Theme" ;
      List list = WebPage.session.find(rq);
      rq = "done" ;
      Theme enrTheme = null;
      
      output.append("<b>Liste des themes : </b><br/><br/>\n");
      
      Iterator it = list.iterator();
      while (it.hasNext()) {
	enrTheme = (Theme) it.next();
	output.append("<a href='"+Path.LOCATION+".Users"+
		      "?theme="+enrTheme.getID()+"'> "+
		      enrTheme.getNom()+"</a><br/>\n");
      }
    } catch (JDBCException e) {
      if (e.getSQLException().getMessage().toString()
	  .contains("Communication link failure")) {
	WebPage.stat.warn("Communication link failure, reboot ....") ;
	
	output.append("IOException ... "+e);
      } else {
	output.append("<br/><i>Impossible d'afficher les themes </i>"+
		      "=> "+rq+"<br/>\n"+e+"<br/>\n"+
		      e.getSQLException()+"<br/>\n");
      }
    }  
  }
}