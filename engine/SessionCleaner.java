package engine ;

import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import java.io.File ;
import constante.Path ;

public class SessionCleaner implements HttpSessionListener {
  /* Session Listener */
  public void sessionCreated(HttpSessionEvent sessionEvent) {
    File temp = new File(Path.getTempDir()+"/"+sessionEvent.getSession().getId());
    if (!temp.mkdir()) {
      temp = null ;
    } else {
      sessionEvent.getSession().setAttribute("temp", temp);
      temp.deleteOnExit();
    }
    System.out.println("temp dir created: " +temp); 
  }
  
  public void sessionDestroyed(HttpSessionEvent sessionEvent) {
    File temp = (File) sessionEvent.getSession().getAttribute("temp");
    if (temp != null) {
      system.SystemTools.remove(temp.toString());
    }
    System.out.println("Session ended: " + sessionEvent.getSession().getId());
  }
}