package engine ;

import java.io.* ;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import util.StringUtil;
import util.XmlBuilder;
import entity.Photo;

import system.SystemTools;

public class Images {
  private static final long serialVersionUID = 1L;

  
  public static XmlBuilder treatIMG(HttpServletRequest request,
				    HttpServletResponse response)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder("img");
    String imgID = StringUtil.escapeHTML(request.getParameter("id")) ;
    
    String mode = request.getParameter("mode") ;
    String rq = null ;
    String filepath = null ;
    String type = null ;
    try {
      rq = "FROM Photo p "+
	" WHERE p.ID = '"+imgID+"' "+
	" AND "+WebPage.restrictToPhotosAllowed(request, "p")+" " ;

      Photo enrPhoto = (Photo) WebPage.session.createQuery(rq).uniqueResult() ;
      rq = "done" ;

      if (enrPhoto == null) {
	output.addException("Cette photo ("+imgID+") n'est pas accessible ou n'existe pas ...");

	return output.validate() ;
      }

      if (enrPhoto.getPath() == null) {
	output.addException("Cette photo ("+imgID+") a un path null ...") ;
	return output.validate() ;
      }
      
      type = ("PETIT".equals(mode) || enrPhoto.getType() == null ? "image/jpeg" : enrPhoto.getType());

      if ("SHRINK".equals(mode)) {
	String width = request.getParameter("width") ;
	try {
	  filepath = SystemTools.shrink(request, enrPhoto, new Integer(width)) ;
	} catch(NumberFormatException e) {
	  output.addException("Impossible de parser le nombre "+width);
	  return output.validate() ;
	}
      } else {
	filepath = ("GRAND".equals(mode) ? enrPhoto.getImagePath() : enrPhoto.getMiniPath()) ;      
      }
      filepath = "file://" + filepath ;
      
      //null = correct, true = incorrect, but contentType already set 
      Boolean correct = sendFile(request, filepath, response, type, output) ;
      if (correct == null || correct) {
	output = null ;
      } else {
	output.validate() ;
      }
    } catch (JDBCException e) {
      e.printStackTrace();
      output.addException("JDBCException", rq);
      output.addException("JDBCException", e.getSQLException());
      output.validate() ;
    } 
    return output ;
  }
  
  protected static Boolean sendFile(HttpServletRequest request,
				    String filepath,
				    HttpServletResponse response,
				    String type,
				    XmlBuilder output) {
    
    boolean uniq = false ;
    try {
      InputStream in = null ; 

      
      filepath = StringUtil.escapeURL(filepath) ;   
      URLConnection conn = new URL(filepath).openConnection() ;
      in = conn.getInputStream() ;
      
      int bufferSize = Math.min(conn.getContentLength(), 4*1024) ;
      byte[] buffer = new byte[bufferSize];
      int nbRead;
      

      uniq = true ;
      response.setHeader( "Content-Disposition", "filename=\"" + new File(filepath).getName() + "\"" );
      response.setContentLength(conn.getContentLength()) ;
      response.setContentType(type); 
      ServletOutputStream out = response.getOutputStream(); 
      while ((nbRead = in.read(buffer)) != -1) {
	out.write(buffer, 0, nbRead);
      }
      out.flush() ;
      out.close() ;

      return null ;
    } catch (MalformedURLException e) {
      e.printStackTrace();
      output.addException("MalformedURLException", filepath);

    } catch (ConnectException e) {
      e.printStackTrace();
      output.addException("ConnectException", filepath);
    } catch (IOException e) {
      WebPage.critic.warn("IOException "+filepath+"("+e.getMessage()+")");
      output.addException("IOException", filepath+"("+e.getMessage()+")");
    }
    return uniq ;
  }
}
