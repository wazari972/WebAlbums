package net.wazari.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

import java.util.logging.Logger;
import net.wazari.service.exchange.Configuration;
import net.wazari.util.system.SystemTools;

public class ImageResizer implements Runnable {
  private static final Logger log = Logger.getLogger(ImageResizer.class.toString());
  private static final int HEIGHT = 200 ;
  
  private Stack<Element> stack = new Stack<Element> () ;
  private boolean initialized = false ;
  private boolean finished = false ;
  private File author ; 
  private boolean done ;
  private Configuration conf ;
  private SystemTools sysTool ;

  public ImageResizer (Configuration conf, SystemTools sysTool) {
      this.conf = conf ;
      this.sysTool = sysTool ;
  }

  public void run () {
    log.info("Starting the ImageResizer Thread") ;
    initialized = true ;
    done = false ;
    Element current ;
    while (!done) {
      log.info("Looping") ;
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
	      log.info("Waiting for the stack to grow");
	      Thread.sleep (1000) ;
	    } catch (InterruptedException e) {}
	}
      } else {
	current = stack.pop() ;
	log.info(current.path);
	
	log.info("Resizing...");
	try {
	    if (!thumbnail(current, conf))
		continue ;
	  
	    log.info("Moving...");
	    if (!move(current, conf))
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
  
  public void terminate () {
    this.finished = true ;
  }
  
  public static void clean (File rep) {
    if (rep.isFile()) {
      if ("Thumbs.db".equals(rep.getName())) {
	rep.delete() ;
      }
      //on fait rien
      
      log.warning("Fichier trouvé "+rep+" !") ;
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
  
    private static boolean move (Element elt, Configuration conf) throws MalformedURLException, URISyntaxException {
      String url = conf.getSourceURL()+conf.getImages() + conf.getSep() +elt.path ;
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
    
  private boolean thumbnail (Element source, Configuration conf) throws URISyntaxException, IOException {
    String path = conf.getSourcePath()+conf.getMini()+conf.getSep()+source.path+".png" ;
    
    File destination = new File (path);
    File parent = destination.getParentFile() ;
    if (!parent.isDirectory() && !parent.mkdirs()) {
      log.warning("Impossible de creer le dossier destination ("+parent+")");
      return false ;
    } else {
      log.warning("Repertoires parents crées ("+parent+")") ;
      String ext = null ;
      int idx = source.image.getName().lastIndexOf('.');
      if (idx != -1) ext = source.image.getName().substring(idx+1);
      return sysTool.thumbnail(source.type, ext, source.image.getAbsolutePath(),
				 destination.getAbsolutePath(),
				 HEIGHT);
    }
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
