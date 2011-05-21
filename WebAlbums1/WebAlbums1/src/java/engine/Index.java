package engine ;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;

import constante.Path;

import util.HibernateUtil;
import util.StringUtil;

import java.io.PrintWriter;
import java.util.*;

import org.hibernate.Transaction;
import org.hibernate.JDBCException;
import org.hibernate.stat.Statistics;
import org.hibernate.exception.SQLGrammarException ;
import entity.Theme;

public class Index {
  
 @SuppressWarnings("unchecked")
 public static void treatVOID(StringBuilder output)
    throws HibernateException {
    //afficher la liste des themes
    String rq = null ; 
    try  {
      StringBuilder str = new StringBuilder () ;
      rq = "from Theme" ;
      Iterator it = WebPage.session.createQuery(rq)
	.setReadOnly(true).setCacheable(true)
	.iterate();
      rq = "done" ;
      Theme enrTheme = null;
      
      str.append("<b>Liste des themes : </b><br/><br/>\n");
      
      while (it.hasNext()) {
	enrTheme = (Theme) it.next();
	str.append("<a href='"+Path.LOCATION+"Users"+
		      "?theme="+enrTheme.getID()+"'> "+
		      enrTheme.getNom()+"</a><br/>\n");
      }

      if (Path.lightenDb()) {
	str.append("<br/>[<a href='"+Path.LOCATION+"Index?action=FULL_IMPORT'>"+
		   "Recharger</a>]<br/>\n");
      }
      
      output.append(str.toString()) ;
    } catch (SQLGrammarException e) {
      Maint.treatFullImport(output) ;
    } catch (JDBCException e) {
      output.append("<br/><i>Impossible d'afficher les themes </i>"+
		    "=> "+rq+"<br/>\n"+e+"<br/>\n"+
		    e.getSQLException()+"<br/>\n");
    }
 }
  
  public static void treat(WebPage.Page page,
			   HttpServletRequest request,
			   HttpServletResponse response)
    throws IOException {
    long debut = System.currentTimeMillis();
    
    Statistics stats = HibernateUtil.sessionFactory.getStatistics();
    stats.setStatisticsEnabled(Path.wantsStats());
    
    WebPage.hash.remove(request) ;
    StringBuilder out = new StringBuilder() ;
    
    boolean isCorrect = false ;
    boolean isPhoto = false ;
    
    StringBuilder err = new StringBuilder () ;
    StringBuilder output = new StringBuilder () ;

    try {
      if (page == WebPage.Page.VOID) {
	treatVOID(output);
      } else if (page == WebPage.Page.MAINT){
	engine.Maint.treatMAINT(request, output);
      } else if (page == WebPage.Page.USER) {
	engine.Users.treatUSR(request, output);
      } else {
	WebPage.updateLogInformation(request, output) ;
	String userID  = engine.Users.getUser(request) ;
	//a partir d'ici, l'utilisateur doit être en memoire
	if (userID != null) { 
	  if (page == WebPage.Page.CHOIX) {
	    engine.Choix.treatCHX(request, output);
	  } else if (page == WebPage.Page.ALBUM) {
	    engine.Albums.treatALBM(request, output);
	  } else if (page == WebPage.Page.PERIODE) {
	    display.Periode.treatPERIODE(request, output) ;
	  } else if (page == WebPage.Page.PHOTO) {
	    engine.Photos.treatPHOTO(request, output);
	  } else if (page == WebPage.Page.CONFIG){
	    engine.Config.treatCONFIG(request, output);
	  } else if (page == WebPage.Page.TAGS){
	    engine.Tags.treatTAGS(request, output);
	  } else if (page == WebPage.Page.IMAGE){
	    isPhoto = engine.Images.treatIMG(request, output, response);
	  } else if (page == WebPage.Page.MAINT){
	    engine.Maint.treatMAINT(request, output);
	  } else {
	    treatVOID(output);
	  }
	} else {
	  treatVOID(output);
	}
      
      }
      isCorrect = true ;
    } catch (JDBCException e) {
      
      err.append("Il y a une erreur dans la requete ... !<br/>\n"+
		 e.getSQLException()+"<br/>\n") ;
      err.append(e);
      
    } catch (HibernateException e) {
      err.append("Problème avec Hibernate ... !<br/<\n" +
		 e+"<br/><br/>**<br/>\n" +
		 StringUtil.escapeHTML(output.toString())+
		 "<br/>**<br/><br/>\n");
      for (int i = 0; i < e.getStackTrace().length; i++) {
	err.append(""+e.getStackTrace()[i]+"</br>");
      }

      err.append(e.getStackTrace().toString());
      
      //rollback if possible
      try {
	Transaction tx = WebPage.session.getTransaction() ;
	if (tx != null && tx.isActive()) {
	  tx.rollback() ;
	}
      } catch (Exception f) {}
      
      //close and reopen the session
      HibernateUtil.closeSession() ;
      WebPage.session = HibernateUtil.currentSession() ;
      
    } catch (WebPage.AccessorsException e) {
      err.append("Problème avec les accesseurs ... !\n" +
		 e+"<br/><br/>**<br/>\n" +
		 StringUtil.escapeHTML(output.toString())+
		 "<br/>**<br/><br/>\n");
    }
    
    long fin = System.currentTimeMillis();

    Header head = WebPage.hash.get(request) ;
    if (!isPhoto) {
      response.setContentType("text/html");
      preventCaching(request, response);
      out.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD "+
		 "HTML 4.01 Transitional//EN\">\n" +
		 "<html>\n" +
		 "  <head>\n" +
		 "    <title>WebAlbum4</title>\n" +
		 (head == null ? "" : head.header())+
		 "  </head>\n" +
		 "  <body "+
		 (head == null ? "" : head.bodyAttributes())+">\n" +
		 (isCorrect ? output.toString() : err.toString())+"<br/>\n");
      if (stats.isStatisticsEnabled()) {
	out.append("Connect: "+ stats.getConnectCount()+ "<br/>\n"+
// Number of flushes done on the session (either by client code or 
// by hibernate).
		   "FlushCount: "+stats.getFlushCount()+ "<br/>\n"+
// The number of completed transactions (failed and successful).
		   "TxCount: "+stats.getTransactionCount()+ "<br/>\n"+
// The number of transactions completed without failure
		   "SuccessfulTransaction: "+stats.getSuccessfulTransactionCount()+ "<br/>\n"+
// The number of sessions your code has opened.
		   "SessionOpen: "+stats.getSessionOpenCount()+ "<br/>\n"+
// The number of sessions your code has closed.
		   "SessionClose: "+stats.getSessionCloseCount()+ "<br/><br/>\n");
// All of the queries that have executed.
	if (Path.wantsQueries()) {
	  int i = 0 ;
	  for (String s : Arrays.asList(stats.getQueries())) {
	    i++ ;
	    out.append("<b>Query "+i+"</b>: "+s+"<br/>\n");
	    out.append("<br>\n");
	  }
	  out.append("UniqueQueryNumber: "+i+ "<br/>\n");
	}
	
// Total number of queries executed.
	out.append("QueryExec: "+stats.getQueryExecutionCount()+ "<br/>\n"+
// Time of the slowest query executed.
		   "QueryExecMaxTime: "+stats.getQueryExecutionMaxTime()+ "<br/><br/>\n"+
// The number of your objects deleted.
		   "EntityDelete: "+stats.getEntityDeleteCount()+ "<br/>\n"+
// The number of your objects fetched.
		   "EntityFetch: "+stats.getEntityFetchCount()+ "<br/>\n"+
// The number of your objects actually loaded (fully populated).
		   "EntityLoad: "+stats.getEntityLoadCount()+ "<br/>\n"+
// The number of your objects inserted.
		   "EntityInsert: "+stats.getEntityInsertCount()+ "<br/>\n"+
// The number of your object updated.
		   "EntityUpdate: "+stats.getEntityUpdateCount()+ "<br/><br/>\n");
      

	double queryCacheHitCount  = stats.getQueryCacheHitCount();
	double queryCacheMissCount = stats.getQueryCacheMissCount();
	double queryCacheHitRatio = queryCacheHitCount / (queryCacheHitCount + queryCacheMissCount) *100;
	out.append("Cache: hit "+(int)queryCacheHitCount+", miss "+(int)queryCacheMissCount+
		   ", ratio "+(int)queryCacheHitRatio+"%<br/></br/>\n"+		   
		   "		<br/><br/>\n");
      }
      out.append("		<br/>Page générée en "+
		 (((double)(fin-debut))/1000)+"s\n" +
		 "  </body>\n" +
		 "</html>");
      stats.clear() ;
      PrintWriter sortie = response.getWriter();
      sortie.println(out.toString());
      sortie.flush();
      sortie.close();
    }
  }  
  protected static void preventCaching(HttpServletRequest request,
				HttpServletResponse response) {
    // see http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
    String protocol = request.getProtocol();
    if ("HTTP/1.0".equalsIgnoreCase(protocol)) {
      response.setHeader("Pragma", "no-cache");
    } else if ("HTTP/1.1".equalsIgnoreCase(protocol)) {
      response.setHeader("Cache-Control", "no-cache"); // "no-store" work also 
    }
    response.setDateHeader("Expires", 0);
  }  
}
