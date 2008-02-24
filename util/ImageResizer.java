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

import traverse.FilesFinder;


public class ImageResizer implements Runnable {
	public static final Logger log = Logger.getLogger("WebAlbum");
	public static final String IMAGES = FilesFinder.ROOT +"/"+ FilesFinder.IMAGES ;
	public static final String MINI   = FilesFinder.ROOT +"/"+ FilesFinder.MINI ;
		
	private String endLocker = "" ;
	
	private Stack<Element> stack = new Stack<Element> () ;
	private boolean finished = false ;
	
	public void run () {
		Element current ;
		while (true) {
			if (stack.empty()) {
				if (finished) {
					synchronized (endLocker) {
						endLocker.notify() ;
					}
					return ;
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
				move  (current);
				log.info("Done !");
			}
		}
	}
	
	public void push (Element elt) {
		stack.push(elt) ;
	}
	
	public void terminate () {
		this.finished = true ;
		synchronized (endLocker) {
			try {
				endLocker.wait() ;
			} catch (InterruptedException e) {}
		}
	}
	
	private static void move (Element elt) {
		File destination = new File (IMAGES + "/"+elt.path);
		destination.getParentFile().mkdirs();
		elt.source.renameTo(destination) ;
	}
	
	private static void resize (Element elt) {
		try {
			
			File destination = new File (MINI+"/"+elt.path+".png");
			destination.mkdirs();
			BufferedImage img = ImageIO.read(elt.source) ;
			
			int largeur ;
			int hauteur ;
			if (img.getWidth() > img.getHeight()) {
				largeur = 266 ;
				hauteur = 200 ;
			} else {
				largeur = 200 ;
				hauteur = 266 ;
			}
			
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
}
