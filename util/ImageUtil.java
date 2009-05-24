package util ;

public interface ImageUtil {
  boolean resize (int height, String source, String dest) ;

  boolean rotate (String degrees, String source, String dest) ;
}