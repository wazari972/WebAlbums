package engine;

import javax.servlet.http.HttpServletRequest;

import constante.Path;
import entity.Theme;
import entity.Utilisateur;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import util.StringUtil;
import util.XmlBuilder;

public class Users {
  private static final long serialVersionUID = 1L;
  
  public static XmlBuilder treatUSR(HttpServletRequest request)
    throws HibernateException {
    XmlBuilder output = new XmlBuilder ("userLogin");
    
    String action = request.getParameter("action") ;
    String themeID = WebPage.getThemeID(request);
    String rq = null ;
    engine.Users.clearUser(request) ;

    boolean valid = false ;
    
    try {
      if ("LOGIN".equals(action)) {
	String userName = StringUtil.escapeHTML(request.getParameter("userName")) ;
	boolean asThemeManager = false ;
	
	if (userName == null) {
	  output.add("denied");
	  output.add("login");
	  return output.validate() ;
	}

	int indexOf = userName.indexOf('+') ;
	if ((indexOf == -1 && userName.equals(WebPage.getThemeName(request))) ||
	  (indexOf != -1  && userName.substring(0, indexOf).equals(WebPage.getThemeName(request))))
	{
	  asThemeManager = true ;
	  if (indexOf != -1) userName = userName.substring(indexOf+1) ;
	}
	
	Utilisateur enrUtil = null ;
	WebPage.log.info("look for user "+userName+" as Admin");
	rq = "FROM Utilisateur WHERE nom = '"+userName+"'" ;
	enrUtil = (Utilisateur) WebPage.session.createQuery(rq)
	  .setReadOnly(true).setCacheable(true)
	  .uniqueResult() ;
	rq = "done" ;
	
	String pass = request.getParameter("userPass") ;
	if (saveUser(request, enrUtil, pass, asThemeManager)) {
	  output.add("valid");
	  valid = true ;
	} else {
	  output.add("denied");
	  output.add("login");
	}
      } else {
	output.add("login");
      }
    } catch (JDBCException e) {
      action = null ;
      e.printStackTrace() ;
      output.cancel() ;
      
      output.addException("JDBCException", rq);
      output.addException("JDBCException", e.getSQLException());
    }
    
    if (valid && Path.lightenDb()) {
      Maint.keepOnlyTheme(output, themeID) ;
    }
    
    return output.validate() ;
  }
      
	
  protected static void clearUser (HttpServletRequest request) {
    request.getSession().setAttribute("LogInID", null) ;
  }
  
  protected static boolean saveUser(HttpServletRequest request,
				    Utilisateur enrUser,
				    String passwd,
				    boolean asThemeManager)
    throws HibernateException {
    String rq = null ;
    String userID ;
    String goodPasswd ;
    String userName ;
    String themeID = WebPage.getThemeID(request) ;
    
    if (themeID == null)
      return false ;
    
    if (asThemeManager) {
      rq = "FROM Theme WHERE id = '"+themeID+"'" ;
      Theme enrTheme = (Theme) WebPage.session.createQuery(rq).uniqueResult() ; 
      rq = "done" ;
      
      if (enrTheme == null) return false ;
      goodPasswd = enrTheme.getPassword() ;

      userName = "Administrateur" ;
      if (enrUser != null) {
	userID = enrUser.getID().toString() ;
	userName += "+"+enrUser.getNom() ;
      }
      else userID = WebPage.USER_CHEAT ;
    } else {
      if (enrUser == null) return false ;
      userName = enrUser.getNom() ;
      userID = Integer.toString(enrUser.getID()) ;
      goodPasswd = null ;
    }
    
    if (goodPasswd != null && !goodPasswd.equals(passwd)) {
      String autoLogin = Path.autoLogin() ;
      if (!themeID.equals(autoLogin)) {
	return false ;
      }
    }
    
    WebPage.log.info("saveUser ("+userName+"-"+userID+")");
    request.getSession().setAttribute("LogInName", userName) ;
    
    request.getSession().setAttribute("LogAsManager", asThemeManager) ;
    request.getSession().setAttribute("LogInName", userName) ;
    request.getSession().setAttribute("LogInID", userID) ;
    request.getSession().setAttribute("inEditionMode", false) ;
    return true ;
  }
  
  public static String getUserID(HttpServletRequest request) {
    String user = (String) request.getSession().getAttribute("LogInID") ;
    if (user == null) {
      user = StringUtil.escapeHTML(request.getParameter("user"));
    }
    return user ;
  }
  public static String getUserName(HttpServletRequest request) {
    return (String) request.getSession().getAttribute("LogInName") ;
    
  }

  public static boolean isLoggedAsCurrentManager(HttpServletRequest request) {
    Boolean isManager  = (Boolean) request.getSession().getAttribute("LogAsManager") ;
    
    if (isManager == null) return false ;
    else return isManager ;
  }
}
