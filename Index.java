import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;

import engine.WebPage;

public class Index extends HttpServlet {

  private static final long serialVersionUID = 7922171598121258405L;
  private WebPage wp  ;
  
  public Index() throws HibernateException {
    super();
    wp = new WebPage() ;
  }
  
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    wp.doGet(request, response) ;
  }
  
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    
    wp.doPost(request, response) ;
  }
  
  protected void preventCaching(HttpServletRequest request,
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
