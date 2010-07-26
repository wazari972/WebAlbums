package util;

import org.apache.log4j.Logger;

import org.hibernate.classic.Session;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.*;

import java.sql.Connection ;

@SuppressWarnings("unchecked")
public class HibernateUtil {
  public static final Logger log = Logger.getLogger("WebAlbum");
  public static final SessionFactory sessionFactory;
  public static final ThreadLocal session = new ThreadLocal();
  public static final Configuration config ;
  static {
    try {
      // Crée la SessionFactory
      log.info("Creation of the configuration") ;
      config = new Configuration().configure() ;
      log.info("Creation of the SessionFactory") ;
      sessionFactory = config.buildSessionFactory();
            
    } catch (Exception e) {
      throw new RuntimeException("Problème de configuration : " + e.getMessage(), e);
    }
  }

    public static Configuration getConfiguration() {
	return config ;
    }
  public static Connection getAConnection() throws Exception {
      return new Configuration().buildSettings().getConnectionProvider().getConnection(); 
  }
  
  public static Session currentSession() throws HibernateException {
    Session s = (Session) session.get();
    // Ouvre une nouvelle Session, si ce Thread n'en a aucune
    if (s == null) {
      s = sessionFactory.openSession();
      session.set(s);
    }
    return s;
  }
  public static void closeSession()
    throws HibernateException {
    Session s = (Session) session.get();
    session.set(null);
    if (s != null)
      s.close();
  }
  public static SessionFactory getSessionFactory() {
	    return sessionFactory;
  }
}