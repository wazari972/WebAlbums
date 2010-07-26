package engine;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;

public class Choix {
  private static final long serialVersionUID = 1L;
  
  public static void treatCHX(HttpServletRequest request,
		       StringBuilder output)
    throws HibernateException {
    output.append(WebPage.getHeadBand(request));
    display.Choix.displayCHX(request, output) ;
  }
}
