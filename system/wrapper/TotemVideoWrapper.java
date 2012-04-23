package system.wrapper ;

import util.ImageUtil;
import system.SystemTools ;

public class TotemVideoWrapper implements ImageUtil {
  public boolean support (String type) {
    return (type != null && type.contains("video"));
  }

  public boolean thumbnail(String source, String dest, int height) {
    return 0 == SystemTools.execWaitFor(new String[] {"totem-video-thumbnailer", "-s", ""+height, source, dest}) ;
  }
  
  public boolean rotate (String degrees, String source, String dest) {
    return true ;
  }

  public boolean shrink(String source, String dest, int width) {
    return true ;
  }

  public void fullscreen(String path) {
    if (path == null) return ;
    SystemTools.exec(new String[] {"totem", path});
  }
}
