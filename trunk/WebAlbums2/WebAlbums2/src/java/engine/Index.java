package engine ;

import java.io.IOException;

import javax.xml.transform.stream.*;
import javax.xml.transform.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;

import constante.Path;

import util.HibernateUtil;

import java.io.PrintWriter;
import java.util.*;

import org.hibernate.Transaction;
import org.hibernate.JDBCException;
import org.hibernate.stat.Statistics;
import org.hibernate.exception.SQLGrammarException ;
import entity.Theme;


import util.XmlBuilder ;
public class Index {
  
 @SuppressWarnings("unchecked")
 public static XmlBuilder treatVOID()
   throws HibernateException {
   XmlBuilder output = new XmlBuilder("index");
   //afficher la liste des themes
   String rq = null ; 
   try  {
     rq = "from Theme" ;
     Iterator it = WebPage.session.createQuery(rq)
       .setReadOnly(true).setCacheable(true)
       .iterate();
     rq = "done" ;
     Theme enrTheme = null;
     while (it.hasNext()) {
       enrTheme = (Theme) it.next () ;
       output.add(new XmlBuilder("theme", enrTheme.getNom()).addAttribut("id", enrTheme.getID()));
     }

     if (Path.lightenDb()) {
       output.add(new XmlBuilder("reload")) ;
     }
      
   } catch (SQLGrammarException e) {
     e.printStackTrace() ;
     
     output.cancel() ;
     Maint.treatFullImport(output) ;
   } catch (JDBCException e) {
     e.printStackTrace() ;

     output.cancel() ;
     output.addException(rq);
     output.addException(e.getSQLException());
   }

   return output.validate() ;
 }
  
  public static void treat(WebPage.Page page,
			   HttpServletRequest request,
			   HttpServletResponse response)
    throws IOException {
    long debut = System.currentTimeMillis();
    request.setCharacterEncoding("UTF-8");
    
    Statistics stats = HibernateUtil.sessionFactory.getStatistics();
    stats.setStatisticsEnabled(Path.wantsStats());
    
    XmlBuilder output = new XmlBuilder("root") ;

    WebPage.tryToSaveTheme(request);
    
    String xslFile = null ;

    boolean isWritten = false ;
    boolean isComplete = false ;
    try {
      xslFile = "static/Display.xsl" ;
      if (page == WebPage.Page.VOID) {
	output.add(treatVOID());
      } else if (page == WebPage.Page.MAINT) {
	xslFile = "static/Empty.xsl" ;

	output.add(engine.Maint.treatMAINT(request));
      } else if (page == WebPage.Page.USER) {
	output.add(engine.Users.treatUSR(request));
      } else {
	String special = request.getParameter("special") ;
	if (special != null) {
	  xslFile = "static/Empty.xsl" ;
	}
	String userID  = engine.Users.getUserID(request) ;
	//a partir d'ici, l'utilisateur doit être en memoire
	if (userID != null) { 
	  if (page == WebPage.Page.CHOIX) {
	    if (special == null) {
	      output.add(engine.Choix.treatCHX(request));
	    } else {
	      output = engine.Choix.treatChxScript(request);
	      isComplete = true ;
	    }
	  } else if (page == WebPage.Page.ALBUM) {
	    output.add(engine.Albums.treatALBM(request));
	  } else if (page == WebPage.Page.PERIODE) {
	    output.add(display.Periode.treatPERIODE(request)) ;
	  } else if (page == WebPage.Page.PHOTO) {
	    output.add(engine.Photos.treatPHOTO(request));
	  } else if (page == WebPage.Page.CONFIG){
	    output.add(engine.Config.treatCONFIG(request));
	  } else if (page == WebPage.Page.TAGS){
	    
	    output.add(engine.Tags.treatTAGS(request));
	  } else if (page == WebPage.Page.IMAGE){
	    XmlBuilder ret = engine.Images.treatIMG(request, response);
	    if (ret == null) {
	      isWritten = true ;
	    } else {
	      output.add(ret);
	    }
	  } else {
	    output.add(treatVOID());
	  }
	} else {
	  WebPage.log.info("special: "+special);
	  if (special == null) {
	    output.add(treatVOID());
	  } else {
	    isComplete = true ;
	    output = new XmlBuilder("nothing");
	  }
	}
      }
      output.validate() ;
    } catch (JDBCException e) {
      e.printStackTrace() ;
      output.cancel() ;

      output.addException("JDBCException", e.getSQLException()) ;
    } catch (HibernateException e) {
      e.printStackTrace() ;
      output.cancel() ;
      
      for (int i = 0; i < e.getStackTrace().length; i++) {
	output.addException("HibernateException", e.getStackTrace()[i]);
      }
      
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
      e.printStackTrace() ;
      output.cancel() ;

      output.addException("AccessorsException", e.getMessage());
    }
    
    long fin = System.currentTimeMillis();
  
    if (!isWritten) {
      preventCaching(request, response);

      if (!isComplete) {
	output.add(WebPage.xmlLogin(request));
	output.add(WebPage.xmlAffichage(request));

	XmlBuilder xmlStats = new XmlBuilder ("stats") ;
	output.add(xmlStats) ;
	xmlStats.add("time", (((float)(fin - debut)/1000)));
	if (stats.isStatisticsEnabled()) {
	  xmlStats.add("Connect", stats.getConnectCount());
// Number of flushes done on the session (either by client code or 
// by hibernate).
	  xmlStats.add("FlushCount", stats.getFlushCount());
// The number of completed transactions (failed and successful).
	  xmlStats.add("TxCount", stats.getTransactionCount());
// The number of transactions completed withoutput failure
	  xmlStats.add("SuccessfulTransaction", stats.getSuccessfulTransactionCount());
// The number of sessions your code has opened.
	  xmlStats.add("SessionOpen", stats.getSessionOpenCount());
// The number of sessions your code has closed.
	  xmlStats.add("SessionClose", stats.getSessionCloseCount());
// All of the queries that have executed.
	  if (Path.wantsQueries()) {
	    XmlBuilder querys = new XmlBuilder("querys");
	    xmlStats.add(querys) ;
	    for (String s : Arrays.asList(stats.getQueries())) {
	    	xmlStats.add("query ", s);
	    }
	  }
	  // Total number of queries executed.
	  xmlStats.add("QueryExec", stats.getQueryExecutionCount());
          // Time of the slowest query executed.
	  xmlStats.add("QueryExecMaxTime", stats.getQueryExecutionMaxTime());
// The number of your objects deleted.
	  xmlStats.add("EntityDelete", stats.getEntityDeleteCount());
// The number of your objects fetched.
	  xmlStats.add("EntityFetch", stats.getEntityFetchCount());
// The number of your objects actually loaded (fully populated).
	  xmlStats.add("EntityLoad", stats.getEntityLoadCount());
// The number of your objects inserted.
	  xmlStats.add("EntityInsert", stats.getEntityInsertCount());
// The number of your object updated.
	  xmlStats.add("EntityUpdate", stats.getEntityUpdateCount());
      

	  double queryCacheHitCount  = stats.getQueryCacheHitCount();
	  double queryCacheMissCount = stats.getQueryCacheMissCount();
	  double queryCacheHitRatio = queryCacheHitCount / (queryCacheHitCount + queryCacheMissCount) *100;
	  xmlStats.add("CacheHit", (int)queryCacheHitCount) ;
	  xmlStats.add("CacheMiss", (int)queryCacheMissCount) ;
	  xmlStats.add("CacheRatio ", (int)queryCacheHitRatio) ;
	}
      }
      doWrite(response, output, xslFile, isComplete);
    }
  }
  private static void doWrite(HttpServletResponse response, XmlBuilder output, String xslFile, boolean isComplete) {
    response.setContentType("text/xml");
    try {
      PrintWriter sortie = response.getWriter();

      if (!isComplete) {
	output.addHeader("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
	output.addHeader("<!DOCTYPE xsl:stylesheet  ["+
			 "<!ENTITY auml   \"&#228;\" >" +
			 "<!ENTITY ouml   \"&#246;\" >" +
			 "<!ENTITY uuml   \"&#252;\" >" +
			 "<!ENTITY szlig  \"&#223;\" >" +
			 "<!ENTITY Auml   \"&#196;\" >" +
			 "<!ENTITY Ouml   \"&#214;\" >" +
			 "<!ENTITY Uuml   \"&#220;\" >" +
			 "<!ENTITY euml   \"&#235;\" >" +
			 "<!ENTITY ocirc  \"&#244;\" >" +
			 "<!ENTITY nbsp   \"&#160;\" >" + 
			 "<!ENTITY Agrave \"&#192;\" >" + 
			 "<!ENTITY Egrave \"&#200;\" >" + 
			 "<!ENTITY Eacute \"&#201;\" >" + 
			 "<!ENTITY Ecirc  \"&#202;\" >" + 
			 "<!ENTITY egrave \"&#232;\" >" + 
			 "<!ENTITY eacute \"&#233;\" >" + 
			 "<!ENTITY ecirc  \"&#234;\" >" + 
			 "<!ENTITY agrave \"&#224;\" >" + 
			 "<!ENTITY iuml   \"&#239;\" >" + 
			 "<!ENTITY ugrave \"&#249;\" >" + 
			 "<!ENTITY ucirc  \"&#251;\" >" + 
			 "<!ENTITY uuml   \"&#252;\" >" + 
			 "<!ENTITY ccedil \"&#231;\" >" + 
			 "<!ENTITY AElig  \"&#198;\" >" + 
			 "<!ENTITY aelig  \"&#330;\" >" + 
			 "<!ENTITY OElig  \"&#338;\" >" + 
			 "<!ENTITY oelig  \"&#339;\" >" + 
			 "<!ENTITY euro   \"&#8364;\">" + 
			 "<!ENTITY laquo  \"&#171;\" >" + 
			 "<!ENTITY raquo  \"&#187;\" >" + 
			 "]>");
	if (Path.wantsXsl()) {
	  output.addHeader("<?xml-stylesheet type=\"text/xsl\" href=\""+xslFile+"\"?>");
	}
      }
      sortie.println(output.toString());      
      
      sortie.flush();
      sortie.close();    
    } catch (IOException e) {
      e.printStackTrace() ;
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
