package util ;

public interface ImageUtil {
    boolean support(String type) ;
    boolean thumbnail (int height, String source, String dest) ;
    
    boolean rotate (String degrees, String source, String dest) ;
}