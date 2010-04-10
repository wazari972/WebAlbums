package util ;

public interface ImageUtil {
  boolean support(String type, String ext) ;
  boolean shrink(String source, String dest, int width);
  boolean thumbnail (String source, String dest, int height) ;  
  boolean rotate (String degrees, String source, String dest) ;
  void fullscreen(String path) ;
}