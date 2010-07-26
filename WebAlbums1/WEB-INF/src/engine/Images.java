package engine ;

import java.io.IOException;
import java.io.InputStream;
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
import constante.Path;
import entity.Photo;
import entity.Theme ;

public class Images {
  private static final long serialVersionUID = 1L;

  public static boolean treatIMG(HttpServletRequest request,
				 StringBuilder output,
				 HttpServletResponse response)
    throws HibernateException {
    boolean uniq = false ;
    String imgID = StringUtil.escapeHTML(request.getParameter("id")) ;
    String mode = request.getParameter("mode") ;
    String rq = null ;
    String filepath = null ;
    String type = null ;
    try {
      rq = "from Photo where id = '"+imgID+"' " +
      	"and id in ("+WebPage.listPhotoAllowed(request)+")" ;
	
      Photo enrPhoto = (Photo) WebPage.session.createQuery(rq).uniqueResult() ;
      rq = "done" ;

      if (enrPhoto == null) {
	String erreur = "<i> Cette photo n'est pas accessible "+
	  "ou n'existe pas ...</i><br/>"+rq+"<br/>\n" ;
	output.append(erreur);
	WebPage.log.warn(erreur) ;
	WebPage.session.flush() ;
	return false ;
      }

      if (enrPhoto.getPath() == null) {
	String erreur = "<i> Cette photo ("+imgID+") a un path null ...</i><br/>\n" ;
	output.append(erreur);
	WebPage.log.fatal(erreur) ;
	return false ;
      }
      
      String themeName ;
      if (!WebPage.isRootSession(request)) {
	themeName = WebPage.getThemeName(request) ;
      } else {
	rq = "select t from Theme t, Album a "+
	  "where t.ID = a.Theme and a.ID = '"+enrPhoto.getAlbum()+"'" ;
	Theme enrTheme = (Theme) WebPage.session.createQuery(rq).uniqueResult() ;
	rq = "done" ;
	if (enrTheme == null) {
	  return false ;
	}
	themeName = enrTheme.getNom () ;
      }
      type = (enrPhoto.getType() == null ? 
	      "image/jpeg" : enrPhoto.getType());

      filepath = Path.getSourceURL() +
	("GRAND".equals(mode) ? Path.IMAGES : Path.MINI)+
	"/"+themeName+"/"+enrPhoto.getPath()+
	("GRAND".equals(mode) ? "" : ".png") ;

      filepath = filepath.replace ("\\","/");
      WebPage.log.debug("open image at "+filepath);
      uniq = sendFile(request, filepath, response, type);
      
    } catch (MalformedURLException e) {
      String erreur = "<i> URL Incorrect' :</i>"+filepath+"<br/>"+e+"<br/>\n" ; 
      output.append(erreur);
      WebPage.log.warn(erreur) ;
    } catch (ConnectException e) {
      String erreur = "<i> Erreur dans la connexion vers' :</i>"+
	filepath+"<br/>"+e+"<br/>\n" ;
      output.append(erreur);
      WebPage.log.warn(erreur) ;
    } catch (JDBCException e) {
      String erreur = "<i> Impossible d'effectuer la requete' :</i>"+
	rq+"<br/>"+e+"<br/>\n"+e.getSQLException()+"<br/>\n" ;
      output.append(erreur);
      WebPage.log.warn(erreur) ;      
    } catch (IOException e) {
      String erreur = "<i> Impossible de lire le fichier ...</i>("+filepath+")\n"+e ;
      output.append(erreur);
      WebPage.log.warn(erreur) ;
    }
    return uniq ;
  }
  
    protected static boolean sendFile(HttpServletRequest request, String filepath, HttpServletResponse response, String type) 
	throws IOException, MalformedURLException, ConnectException {
    boolean uniq = false ;
    
    InputStream in = null ; 
    
    filepath = StringUtil.escapeURL(filepath) ;

    URLConnection conn = new URL(filepath).openConnection() ;
    in = conn.getInputStream() ;
    
    response.setContentLength(conn.getContentLength()) ;
    response.setContentType(type); 
    
    int bufferSize = (int) Math.min(conn.getContentLength(), 4*1024) ;
    byte[] buffer = new byte[bufferSize];
    int nbRead;
    
    uniq = true ;
    ServletOutputStream out = response.getOutputStream(); 
    while ((nbRead = in.read(buffer)) != -1) {
      out.write(buffer, 0, nbRead);
    }
    out.flush() ;
    out.close() ;
    
    return uniq ;
  }
}
