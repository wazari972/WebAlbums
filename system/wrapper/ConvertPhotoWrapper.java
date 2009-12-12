package system.wrapper ;

import util.ImageUtil;
import system.SystemTools ;

public class ConvertPhotoWrapper implements ImageUtil {
  public boolean support (String type) {
    return (type != null && type.contains("image"));
  }

  public boolean shrink(String source, String dest, int width) {
    return 0 == SystemTools.execWaitFor(new String[] {"convert", "-resize", ""+width+"x", source, dest}) ;
  }

  public boolean thumbnail(String source, String dest, int height) {
    return 0 == SystemTools.execWaitFor(new String[] {"convert", "-thumbnail", "x"+height, source, dest}) ;
  }


  public boolean rotate (String degrees, String source, String dest) {
    return 0 == SystemTools.execWaitFor(new String[] {"convert", "-rotate", degrees, source, dest}) ;
  }

  public void fullscreen(String path) {
    if (path == null) return ;
    SystemTools.exec(new String[] {"eog", "--fullscreen", path});
  }
}