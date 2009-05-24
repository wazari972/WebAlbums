package util;

//Updates: 2002.06.26


import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;


/**
 * Propose des méthodes statiques pour manipuler des images.
 */
public class ImgTool implements ImageUtil {
  /** 
   * Redimensionne une image.
   * 
   * @param source Image à redimensionner.
   * @param width Largeur de l'image cible.
   * @param height Hauteur de l'image cible.
   * @return Image redimensionnée.
   */
  @SuppressWarnings("unused")
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

  @SuppressWarnings("unused")
  public boolean resize (int height, String source, String dest) {
    try {
      Image img = ImageIO.read(new File(source)) ;
      
      int hauteur = img.getHeight(null) ;
      int largeur = img.getWidth(null) ;
      
      log.info("OLD h:"+hauteur+" et l:"+largeur);
      //conserver les proportions des images
      if (hauteur > largeur) {
	double reduc = (heigth/(double)hauteur) ;
	largeur = new Double ((double) largeur * reduc).intValue() ;
	hauteur = 266 ;
	log.info("h=266, "+reduc);
      } else {
	double reduc = (heigth/(double)largeur) ;
	hauteur = new Double((double) hauteur * reduc).intValue() ;
	largeur = heigth ;
	log.info("l=266, "+reduc);
      }
      
      log.info("NEW h:"+hauteur+" et l:"+largeur);
      
      BufferedImage buff ;
      if (true) {
	buff = scale (img, largeur, hauteur) ;
      } else {
	Image image = img.getScaledInstance(largeur, hauteur, Image.SCALE_FAST) ;
	buff = ImgTool.getBufferedImage(image) ;
      }
      
      File des = new File(dest) ;
      ImageIO.write(buff, "png", des) ;
      return true ;
    } catch (IOException e) {
      e.printStackTrace();
      return false ;
    }
  }
  
	/**
	 * Crée une BufferedImage à partir d'une Image.
	 */
	public static BufferedImage getBufferedImage(Image img) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		BufferedImage bImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bImage.createGraphics();
		g2.drawImage(img, 0, 0, null);
		return bImage;
	}  


	/**
	 * Crée une BufferedImage à partir d'un tableau de booléens.
	 */
	public static BufferedImage getBufferedImage(boolean[][] img) {
		int w = img.length;
		int h = img[0].length;
		BufferedImage bImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (img[i][j]) bImage.setRGB(i, j, 0xff000000);
				else bImage.setRGB(i, j, 0xffffffff);
			}
		}
		return bImage;
	}  


	/**
	 * Crée une BufferedImage à partir d'un tableau d'entiers, chacun étant 
	 * l'indice de la couleur à appliquer.
	 */
	public static BufferedImage getBufferedImage(int[][] img, Color[] color) {
		int w = img.length;
		int h = img[0].length;
		BufferedImage bImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int index = img[i][j];
				if ((index >= 0) && (index < color.length)) {
					Color c = color[index];
					bImage.setRGB(i, j, c.getRGB());
				}
			}
		}
		return bImage;
	}   


	/**
	 * Copie une BufferedImage
	 */
	public static BufferedImage copy(BufferedImage bImage) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);    
		BufferedImage bImage2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bImage2.createGraphics();
		g2.drawImage(bImage, 0, 0, null);
		return bImage2;
	}


	/**
	 * Agrandi une image de telle manière à ce qu'elle devienne carrée. Les 
	 * nouveaux pixels sont blancs.
	 */
	public static BufferedImage squared(BufferedImage bImage) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);
		int max = (w > h ? w : h);
		BufferedImage bImage2 = new BufferedImage(max, max, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < max; i++) {
			for (int j = 0; j < max; j++) {
				bImage2.setRGB(i, j, 0xffffffff);
			}
		}
		Graphics2D g2 = bImage2.createGraphics();
		g2.drawImage(bImage, (max-w)/2, (max-h)/2, null);
		return bImage2;
	}


	/**
	 * Agrandi une image de telle manière à ce qu'elle devienne carrée, et 
	 * que son côté soit égal à la digonale de l'image. Les nouveaux pixels 
	 * sont blancs. Cette méthode est utilisée avant une rotation, afin de ne 
	 * pas perdre des pixels.
	 */ 
	public static BufferedImage diagSized(BufferedImage bImage) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);
		int diag = (int)(Math.sqrt(w*w+h*h));
		BufferedImage bImage2 = new BufferedImage(diag, diag, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < diag; i++) {
			for (int j = 0; j < diag; j++) {
				bImage2.setRGB(i, j, 0xffffffff);
			}
		}
		Graphics2D g2 = bImage2.createGraphics();
		g2.drawImage(bImage, (diag-w)/2, (diag-h)/2, null);
		return bImage2;
	}


	/**
	 * Crée une image en niveaux de gris.
	 */
	public static BufferedImage grayScale(BufferedImage bImage) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);
		BufferedImage bImage2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int p = bImage.getRGB(i, j);
				int a = (((p >> 16) & 0xff) + ((p >> 8) & 0xff) + (p & 0xff)) / 3;
				bImage2.setRGB(i, j, (0xff << 24) | (a << 16) | (a << 8) | a);
			}
		}
		return bImage2;
	}


	/**
	 * Crée une image binaire, en utilisant le seuil spécifié.
	 */
	public static BufferedImage binary(BufferedImage bImage, int threshold) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);
		BufferedImage bImage2 = grayScale(bImage);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if ((bImage2.getRGB(i, j) & 0xff) > threshold) bImage2.setRGB(i, j, 0xffffffff);
				else bImage2.setRGB(i, j, 0xff000000);
			}
		}
		return bImage2;
	}


	/**
	 * Crée une image binaire, en calculant le seuil automatiquement à partir
	 * de l'histogramme.
	 */
	public static BufferedImage binary(BufferedImage bImage) {
		final int n = 51; // size of the filter, must be uneven
		int space = (n-1)/2;
		int[] histo = histogram(bImage);
		int[] hBig = new int[histo.length+2*space];
		for (int i = 0; i < histo.length; i++) {
			hBig[i+space] = histo[i];
		}
		int[] histo2 = new int[histo.length];
		for (int i = 0; i < histo.length; i++) {
			int sum = 0;
			for (int j = 0; j < n; j++) {
				sum += hBig[i+j];
			}
			histo2[i] = sum/n;
		}
		int[] min = minimums(histo2);
		int threshold = 0;
		for (int i = 0; i < min.length; i++) {
			if (min[i] > threshold) threshold = min[i];
		}
		return binary(bImage, threshold);
	}


	/**
	 * Crée une image binaire sous forme d'un tableau de booléens, en
	 * utilisant le seuil spécifié.
	 */
	public static boolean[][] getBoolean(BufferedImage bImage, int threshold) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);
		BufferedImage bImage2 = grayScale(bImage);
		boolean[][] image = new boolean[w][h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if ((bImage2.getRGB(i, j) & 0xff) <= threshold) image[i][j] = true;
			}
		}
		return image;
	}


	/**
	 * Crée une image binaire sous forme d'un tableau de booléens, en
	 * calculant le seuil automatiquement à partir de l'histogramme.
	 */
	public static boolean[][] getBoolean(BufferedImage bImage) {
		return getBoolean(binary(bImage), 127);
	}


	/**
	 * Remplace les pixels de valeur 0 (fond de l'image) par des pixels
	 * blancs.
	 */
	public static BufferedImage whiteBackground(BufferedImage bImage) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);
		BufferedImage bImage2 = copy(bImage);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (bImage2.getRGB(i, j) == 0) bImage2.setRGB(i, j, 0xffffffff);
			}
		}
		return bImage2;
	}


	/**
	 * Retourne le plus petit rectangle pouvant contenir tous les pixels 
	 * différents du pixel blanc. Attention: Un pixel de fond n'est pas un 
	 * pixel blanc !
	 */
	public static Rectangle getBounds(BufferedImage bImage) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);
		int iMin = 1000000;
		int jMin = 1000000;
		int iMax = -1;
		int jMax = -1;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (bImage.getRGB(i, j) != 0xffffffff) {
					if (i < iMin) iMin = i;
					if (i > iMax) iMax = i;
					if (j < jMin) jMin = j;
					if (j > jMax) jMax = j;
				}
			}
		}
		if (iMax < 0) return new Rectangle(0, 0, 0, 0); // no points...
		return new Rectangle(iMin, jMin, iMax-iMin, jMax-jMin);
	}  


	/**
	 * Tourne l'image autour de son centre. L'angle est spécifié en degrés.
	 * Le booléen whiteBackground spécifie si il faut que les nouveaux pixels
	 * générés par la rotation soient coloriés en blanc ou pas.
	 */
	public static BufferedImage rotate(BufferedImage bImage, int angle, boolean whiteBackground) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);    
		AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(angle), w/2, h/2);
		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		BufferedImage bImage2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);     
		op.filter(bImage, bImage2);
		if (whiteBackground) bImage2 = ImgTool.whiteBackground(bImage2);
		return bImage2;
	}               


	/**
	 * Compte le nombre de pixels différents du pixel blanc. Attention: Un 
	 * pixel de fond n'est pas un pixel blanc !
	 */
	public static int surface(BufferedImage bImage) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);
		int sum = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				if (bImage.getRGB(i, j) != 0xffffffff) sum++;
			}
		}
		return sum;
	}  


	/**
	 * Calcule l'histogramme d'une image, sous forme d'un tableau de 256 
	 * entiers. Une image couleur est d'abord transformée en niveaux de gris.
	 */
	public static int[] histogram(BufferedImage bImage) {
		int w = bImage.getWidth(null);
		int h = bImage.getHeight(null);
		BufferedImage bImage2 = grayScale(bImage);    
		int[] histo = new int[256];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int color = bImage2.getRGB(i, j) & 0xff;
				histo[color]++;
			}
		}
		return histo;
	} 


	/**
	 * Retourne un tableau contenant les minimums d'une fonction.
	 */
	private static int[] minimums(int[] function) {
		final int up = 1;
		final int down = -1;
		ArrayList<Integer> values = new ArrayList<Integer>();
		int x = 0;
		int way = 0;
		for (int i = 0; i < function.length; i++) {
			if (function[i] > x) {
				if (way == down) values.add(i-1);
				x = function[i];
				way = up;
			}
			if (function[i] < x) {
				x = function[i];
				way = down;
			}
		}
		int[] vector = new int[values.size()];
		for (int i = 0; i < vector.length; i++) {
			vector[i] = values.get(i);
		}
		return vector;
	}
}