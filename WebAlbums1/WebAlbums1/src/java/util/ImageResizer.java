package util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import constante.Path;

public class ImageResizer implements Runnable {
  private static final int HEIGHT = 200 ;
  private static final FileWrapper fileWrapper = new FileWrapper () ;
  
  public static final Logger log = Logger.getLogger("ImageResize");
  
    static {
      try {
      log.addAppender(
	new FileAppender(new SimpleLayout(),
			 "/tmp/ImageResize.log"));
      
      } catch (Exception e) {}
      log.fatal("====================\n\n");
      fileWrapper.addWrapper(new ConvertPhotoWrapper()) ;
      fileWrapper.addWrapper(new TotemVideoWrapper()) ;
    }

  private Stack<Element> stack = new Stack<Element> () ;
  private boolean initialized = false ;
  private boolean finished ;
  private File author ; 
  private boolean done ;
  public void run () {
    initialized = true ;
    done = false ;
    finished = false ;
    Element current ;
    while (!done) {
      if (stack.empty()) {
	if (finished) {
	  if (this.author != null && this.author.isDirectory()) {
	    log.info("Nettoyage du dossier "+this.author) ;
	    File[] lst = this.author.listFiles() ;
	    
	    //supprimer recursivement tous les dossiers de ce repertoire
	    for (File f : lst) {
	      clean (f) ;
	    }
	  } else {
	      log.info("Pas de dossier à nettoyer");
	  }
	  done = true ;
	} else {
	    //if not finished, wait a second
	    try {
		Thread.sleep (1000) ;
	    } catch (InterruptedException e) {}
	}
      } else {
	current = stack.pop() ;
	log.info(current.path);
	
	log.info("Resizing...");
	try {
	    if (!thumbnail(current))
		continue ;
	  
	    log.info("Moving...");
	    if (!move(current))
		continue ;
	    
	    log.info("Done !");
	} catch (URISyntaxException e) {
	    log.info("URISyntaxException "+e);
	} catch (MalformedURLException e) {
	    log.info("MalformedURLException "+e);
	} catch (IOException e) {
	    log.info("IOExceptionLException "+e);
	}
      }
    }
    log.info("Finished !");
  }
  
  public void push (Element elt) {
    stack.push(elt) ;
  }
  
  public void terminate (File author) {
    this.finished = true ;
    this.author = author ;
  }
  
  public static void clean (File rep) {
    if (rep.isFile()) {
      if ("Thumbs.db".equals(rep.getName())) {
	rep.delete() ;
      }
      //on fait rien
      
      log.warn("Fichier trouvé "+rep+" !") ;
    } else if (rep.isDirectory()) {
      log.info("Suppression du dossier "+rep+" ...") ;
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
    String url = Path.getSourceURL()+Path.IMAGES + Path.SEP +elt.path ;
    log.info("SOURCE = "+url);
    URI uri = new URL(StringUtil.escapeURL(url)).toURI();
    File destination = new File (uri);
    destination.getParentFile().mkdirs();
    log.info("Move "+elt.image + " to "+destination) ;

    if (!elt.image.renameTo(destination)) {
      log.info("Impossible de déplacer ...");
      return false ;
    }

    return true ;
  }
    
    private static boolean thumbnail (Element source) throws URISyntaxException, IOException {
    String path = Path.getSourcePath()+Path.MINI+Path.SEP+source.path+".png" ;
    
    File destination = new File (path);
    File parent = destination.getParentFile() ;
    if (!parent.isDirectory() && !parent.mkdirs()) {
      log.warn("Impossible de creer le dossier destination ("+parent+")");
      return false ;
    } else {
      log.warn("Repertoires parents crées ("+parent+")") ;
      fileWrapper.setCurrentType(source.type);
      return fileWrapper.thumbnail(HEIGHT, source.image.getAbsolutePath(), destination.getAbsolutePath());
    }
  }

    public static boolean support(String type) {
	return fileWrapper.support(type);
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
