package engine;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;

import util.XmlBuilder;

public class Choix {
  private static final long serialVersionUID = 1L;
  
  public static XmlBuilder treatCHX(HttpServletRequest request)
    throws HibernateException {
    return display.Choix.displayCHX(request) ;
  }
  public static XmlBuilder treatChxScript(HttpServletRequest request)
    throws HibernateException {
    return display.Choix.displayChxScript(request) ;
  }
}
