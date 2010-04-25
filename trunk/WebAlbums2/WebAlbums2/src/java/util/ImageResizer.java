package util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

import engine.WebPage;
import constante.Path;
import system.SystemTools ;

public class ImageResizer implements Runnable {
  private static final int HEIGHT = 200 ;
  
  private Stack<Element> stack = new Stack<Element> () ;
  private boolean initialized = false ;
  private boolean finished = false ;
  private File author ; 
  private boolean done ;
  
  public void run () {
    WebPage.other.info("Starting the ImageResizer Thread") ;
    initialized = true ;
    done = false ;
    Element current ;
    while (!done) {
      WebPage.other.info("Looping") ;
      if (stack.empty()) {
	if (finished) {
	  if (this.author != null && this.author.isDirectory()) {
	    WebPage.log.info("Nettoyage du dossier "+this.author) ;
	    File[] lst = this.author.listFiles() ;
	    
	    //supprimer recursivement tous les dossiers de ce repertoire
	    for (File f : lst) {
	      clean (f) ;
	    }
	  } else {
	      WebPage.log.info("Pas de dossier à nettoyer");
	  }
	  done = true ;
	} else {
	    //if not finished, wait a second
	    try {
	      WebPage.log.info("Waiting for the stack to grow");
	      Thread.sleep (1000) ;
	    } catch (InterruptedException e) {}
	}
      } else {
	current = stack.pop() ;
	WebPage.log.info(current.path);
	
	WebPage.log.info("Resizing...");
	try {
	    if (!thumbnail(current))
		continue ;
	  
	    WebPage.log.info("Moving...");
	    if (!move(current))
		continue ;
	    
	    WebPage.log.info("Done !");
	} catch (URISyntaxException e) {
	    WebPage.log.info("URISyntaxException "+e);
	} catch (MalformedURLException e) {
	    WebPage.log.info("MalformedURLException "+e);
	} catch (IOException e) {
	    WebPage.log.info("IOExceptionLException "+e);
	}
      }
    }
    WebPage.log.info("Finished !");
  }
  
  public void push (Element elt) {
    stack.push(elt) ;
  }
  
  public void terminate () {
    this.finished = true ;
  }
  
  public static void clean (File rep) {
    if (rep.isFile()) {
      if ("Thumbs.db".equals(rep.getName())) {
	rep.delete() ;
      }
      //on fait rien
      
      WebPage.log.warn("Fichier trouvé "+rep+" !") ;
    } else if (rep.isDirectory()) {
      WebPage.log.info("Suppression du dossier "+rep+" ...") ;
      File[] lst = rep.listFiles() ;
      
      //supprimer recursivement tous les dossiers vides de ce repertoire
      for (File f : lst) {
	clean (f) ;
      }
      //et supprimer le repertoire lui meme
      rep.delete() ;
    }
  }
  
    private static boolean move (Element elt) throws MalformedURLException, URISyntaxException {
      String url = Path.getSourceURL()+Path.getImages() + Path.SEP +elt.path ;
      WebPage.log.info("SOURCE = "+url);
      URI uri = new URL(StringUtil.escapeURL(url)).toURI();
      File destination = new File (uri);
      destination.getParentFile().mkdirs();
      WebPage.log.info("Move "+elt.image + " to "+destination) ;
      
      if (!elt.image.renameTo(destination)) {
	WebPage.log.info("Impossible de déplacer ...");
	return false ;
      }

    return true ;
  }
    
  private static boolean thumbnail (Element source) throws URISyntaxException, IOException {
    String path = Path.getSourcePath()+Path.getMini()+Path.SEP+source.path+".png" ;
    
    File destination = new File (path);
    File parent = destination.getParentFile() ;
    if (!parent.isDirectory() && !parent.mkdirs()) {
      WebPage.log.warn("Impossible de creer le dossier destination ("+parent+")");
      return false ;
    } else {
      WebPage.log.warn("Repertoires parents crées ("+parent+")") ;
      String ext = null ;
      int idx = source.image.getName().lastIndexOf('.');
      if (idx != -1) ext = source.image.getName().substring(idx+1);
      ImageUtil util = SystemTools.getWrapper(source.type, ext) ;
      if (util == null) return false ;
      else return util.thumbnail(source.image.getAbsolutePath(),
				 destination.getAbsolutePath(),
				 HEIGHT);
    }
  }

  public static boolean support(String type, String ext) {
    return SystemTools.getWrapper(type, ext) != null ;
  }
  
  public static class Element {
    public String path ;
    public File image ;
    public String type ;
    public Element(String path, File image, String type) {
      this.path = path;
      this.image = image;
      this.type = type;
    } 
  }
  
  public boolean isDone() {
    return !initialized || done;
  }
  
  public int getStackSize() {
    return this.stack.size();
  }
}
