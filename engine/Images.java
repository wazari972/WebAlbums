package engine ;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import util.StringUtil;
import constante.Path;
import entity.Photo;
import entity.Theme ;

public class Images extends HttpServlet {
  private static final long serialVersionUID = 1L;
  
  public void init() {
    Path.setLocation(this) ;
  }
  
  public void doGet(HttpServletRequest request,
		    HttpServletResponse response)
    throws ServletException, IOException {
    
    WebPage.treat(WebPage.Page.IMG, request, response) ;
  }
  public void doPost(HttpServletRequest request,
		     HttpServletResponse response)
    throws ServletException, IOException {
    doGet(request, response) ;
  }
  
  static boolean treatIMG(HttpServletRequest request,
			  StringBuilder output,
			  HttpServletResponse response)
    throws HibernateException {
    boolean uniq = false ;
    String imgID = StringUtil.escapeHTML(request.getParameter("id")) ;
    String mode = request.getParameter("mode") ;
    String rq = null ;
    String filepath = null ;
    try {
      rq = "from Photo where id = '"+imgID+"' "+
	"and id in ("+WebPage.listPhotoAllowed(request)+")" ;
      Photo enrPhoto = (Photo) WebPage.session.find(rq).iterator().next() ;
      rq = "done" ;

      String themeName ;
      if (!WebPage.isRootSession(request)) {
	themeName = WebPage.getThemeName(request) ;
      } else {
	rq = "select t from Theme t, Album a "+
	  "where t.ID = a.Theme and a.ID = '"+enrPhoto.getAlbum()+"'" ;
	Theme enrTheme = (Theme) WebPage.session.find(rq).iterator().next() ;
	themeName = enrTheme.getNom () ;
      }

      filepath = Path.getSourceURL() +
	("GRAND".equals(mode) ? Path.IMAGES : Path.MINI)+
	"/"+themeName+"/"+enrPhoto.getPath()+
	("GRAND".equals(mode) ? "" : ".png") ;
            
      uniq = sendImage(request, filepath, response);
            
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
    } catch (NoSuchElementException e) {
      String erreur = "<i> Cette photo n'est pas accessible "+
	"ou n'existe pas ...</i><br/>"+rq+"<br/>\n" ;
      output.append(erreur);
      WebPage.log.warn(erreur) ;
    } catch (IOException e) {
      String erreur = "<i> Impossible de lire le fichier ...</i>("+filepath+")\n"+e ;
      output.append(erreur);
      WebPage.log.warn(erreur) ;
    }
    return uniq ;
  }
  
  protected static boolean sendImage(HttpServletRequest request, String filepath, HttpServletResponse response) throws IOException, MalformedURLException, ConnectException {
    boolean uniq = false ;
    
    InputStream in = null ; 
    
    filepath = StringUtil.escapeURL(filepath) ;
    //dans mon cas le filepath et le path complet après création d'un temp file 
    WebPage.log.warn("on ouvre la connexion vers  : "+filepath) ;
    URLConnection conn = new URL(filepath).openConnection() ;
    in = conn.getInputStream() ;
    
    WebPage.log.warn("size : "+conn.getContentLength()) ;
    
    response.setContentLength(conn.getContentLength()) ;
    response.setContentType("image/jpeg"); 
    //response.setHeader("Content-Disposition", "attachment;filename=\"" + file.getName() + "\""); 
    
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
