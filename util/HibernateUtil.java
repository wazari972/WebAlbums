package util;

import org.apache.log4j.Logger;

import org.hibernate.classic.Session;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.*;

public class HibernateUtil {
  public static final Logger log = Logger.getLogger("WebAlbum");
  private static final SessionFactory sessionFactory;
  public  static final ThreadLocal session = new ThreadLocal();
  
  static {
    try {
      // Crée la SessionFactory
      log.info("creation") ;
      sessionFactory = new Configuration().configure().buildSessionFactory();
      log.info("created") ;
      
    } catch (HibernateException ex) {
      throw new RuntimeException("Problème de configuration : " + ex.getMessage(), ex);
    } catch (Exception e) {
      System.out.println("meeerde ") ;
      throw new RuntimeException("Problème de configuration : " + e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  public static Session currentSession() throws HibernateException {
    Session s = (Session) session.get();
    // Ouvre une nouvelle Session, si ce Thread n'en a aucune
    if (s == null) {
      s = sessionFactory.openSession();
      session.set(s);
    }
    return s;
  }
  
  @SuppressWarnings("unchecked")
  public static void closeSession() throws HibernateException {
    Session s = (Session) session.get();
    session.set(null);
    if (s != null)
      s.close();
  }
	
  public static SessionFactory getSessionFactory() {
	    return sessionFactory;
  }
}