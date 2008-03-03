package util;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import constante.Path;


public class ImageResizer implements Runnable {
	public static final Logger log = Logger.getLogger("WebAlbum");
	
	private Stack<Element> stack = new Stack<Element> () ;
	private boolean finished = false ;
	private File author ; 
	private boolean done = false ;
	public void run () {
		Element current ;
		while (!done) {
			if (stack.empty()) {
				if (finished) {
					if (this.author.isDirectory()) {
						log.info("Nettoyage du dossier "+this.author) ;
						File[] lst = this.author.listFiles() ;
						
						//supprimer recursivement tous les dossiers de ce repertoire
						for (File f : lst) {
							delete (f) ;
						}
					}
					done = true ;
				} else {
					try {
						Thread.sleep (1000) ;
					} catch (InterruptedException e) {}
				}
			} else {
				current = stack.pop() ;
				log.info(current.path);
				log.info("Resizing...");
				resize(current);
				log.info("Moving...");
				move(current);
				log.info("Done !");
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
	
	public static void delete (File rep) {
		if (rep.isFile()) {
			//on fait rien
			log.warn("Fichier trouvé "+rep+" !") ;
		} else if (rep.isDirectory()) {
			log.info("Suppression du dossier "+rep+" ...") ;
			File[] lst = rep.listFiles() ;
			
			//supprimer recursivement tous les dossiers de ce repertoire
			for (File f : lst) {
				delete (f) ;
			}
			//et supprimer le repertoire lui meme
			rep.delete() ;
		}
	}
	
	private static void move (Element elt) {
		File destination = new File (Path.ABSOLUTE_PATH+Path.IMAGES + Path.SEP +elt.path);
		destination.getParentFile().mkdirs();
		elt.source.renameTo(destination) ;
	}
	
	private static void resize (Element elt) {
		try {
			
			File destination = new File (Path.ABSOLUTE_PATH+Path.MINI+Path.SEP+elt.path+".png");
			destination.mkdirs();
			BufferedImage img = ImageIO.read(elt.source) ;
			
			int hauteur = img.getHeight() ;
			int largeur = img.getWidth() ;

			//conserver les proportions des images
			if (hauteur > largeur) {
				double reduc = (266.0/(double)hauteur) ;
				largeur = new Double ((double) largeur * reduc).intValue() ;
				hauteur = 266 ;
				log.info("## "+reduc);
			} else {
				double reduc = (266.0/(double)largeur) ;
				hauteur = new Double((double) hauteur * reduc).intValue() ;
				largeur = 266 ;
				log.info("** "+reduc);
			}
			log.info("h:"+img.getHeight()+" et h2:"+hauteur);
			log.info("l:"+img.getWidth()+" et l2:"+largeur);
			BufferedImage res = scale (img, largeur, hauteur) ;
			
			File des = destination ;
			ImageIO.write(res, "png", des) ;
				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** 
	 * Redimensionne une image.
	 * 
	 * @param source Image à redimensionner.
	 * @param width Largeur de l'image cible.
	 * @param height Hauteur de l'image cible.
	 * @return Image redimensionnée.
	 */
	private static BufferedImage scale(Image source, int width, int height) {
	    /* On crée une nouvelle image aux bonnes dimensions. */
	    BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

	    /* On dessine sur le Graphics de l'image bufferisée. */
	    Graphics2D g = buf.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.drawImage(source, 0, 0, width, height, null);
	    g.dispose();

	    /* On retourne l'image bufferisée, qui est une image. */
	    return buf;
	}

	public static class Element {
		public String path ;
		public File source ;
		
		public Element(String path, File source) {
			this.path = path;
			this.source = source;
		}
	
	}

	public boolean isDone() {
		return done;
	}
}
