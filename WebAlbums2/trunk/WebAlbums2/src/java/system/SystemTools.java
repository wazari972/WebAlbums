package system;

import java.util.Iterator ;
import java.util.Arrays ;
import java.util.List ;
import java.util.ArrayList ;
import util.ImageUtil ;
import org.apache.log4j.Logger;
import engine.WebPage ;
import entity.Photo ;
import org.hibernate.Query;
import javax.servlet.http.HttpServletRequest;
import engine.Users ;
import java.io.*;

import system.wrapper.* ;

public class SystemTools {
  private static List<ImageUtil> wrappers = new ArrayList<ImageUtil>(2) ;
  private static final Logger log = Logger.getLogger("Process");

  static {
    addWrapper(new ConvertPhotoWrapper()) ;
    addWrapper(new TotemVideoWrapper()) ;
  }
  
  public static void addWrapper (ImageUtil wrapper) {
    wrappers.add(wrapper) ;
  }
  
  public static ImageUtil getWrapper(String type, String ext) {
    for (ImageUtil util : wrappers) {
      if (util.support(type, ext)) {
	return util ;
      }
    }
    WebPage.log.warn("no wrapper for "+type);
    return getWrapper("image", null) ;
  }

  private static File buildTempDir(HttpServletRequest request, String type, String id) {
    File root = (File) request.getSession().getAttribute("temp");
    if (!root.isDirectory() && !root.mkdir()) return null;
    
    //build temp/USER
    File dir = new File(root, Users.getUserName(request)) ;
    if (!dir.isDirectory() && !dir.mkdir()) return null;
    dir.deleteOnExit() ;
    
    //build temp/user/THEME
    dir = new File(dir, WebPage.getThemeName(request)) ;
    if (!dir.isDirectory() && !dir.mkdir()) return null;
    dir.deleteOnExit() ;
    
    //build temp/user/theme/TYPE
    dir = new File(dir, type) ;
    if (!dir.isDirectory() && !dir.mkdir()) return null;
    dir.deleteOnExit() ;
    
    if (id != null) {
      //build temp/user/theme/type/idID
      File unique = new File(dir, "id"+id) ;
      if (!unique.isDirectory() && !unique.mkdir()) {
	try {
	  unique = File.createTempFile("uid.",".tags", dir) ;
	  unique.delete();
	  if (!unique.mkdir ()) return null ;
	} catch (IOException e) {e.printStackTrace();return null ;}	
      }
      dir = unique ;
      dir.deleteOnExit() ;
    }
    
    return dir ;
  }
  
  public static void fullscreen(Query query, String type, String id, String page, HttpServletRequest request) {
    Integer ipage ;
    try {
      ipage = new Integer (page);
    } catch (Exception e) {ipage = 0 ;}
    
    File dir = null ;
    int i = 0 ;
    boolean first = true ;
    Iterator itPhoto = query.iterate() ;
    ImageUtil util = getWrapper("image", null) ;
    while(itPhoto.hasNext()) {
      if (first) {
	dir = buildTempDir(request, type, id);
	if (dir == null) return ;
      }

      Photo enrPhoto = (Photo) itPhoto.next();
      int currentPage = i / WebPage.TAILLE_PHOTO ;
      File fPhoto = new File(dir, ""+i+"-p"+currentPage+"-"+enrPhoto.getID()+"."+enrPhoto.getExtention()) ;
      link(enrPhoto.getImagePath(), fPhoto) ;

      if (first && ipage.equals(currentPage)) {
	util.fullscreen(fPhoto.toString());
	first = false ;
      }
      fPhoto.deleteOnExit() ;
      i++ ;
    }
  }

  public static String shrink(HttpServletRequest request, Photo enrPhoto, int width) {
    if (width >= enrPhoto.getWidth(true)) {
      return enrPhoto.getImagePath() ;
    }
    
    File dir = buildTempDir(request, "shrinked", null);
    if (dir == null) return null ;
    
    File fPhoto = new File(dir, enrPhoto.getID()+"-"+width+"."+enrPhoto.getExtention()) ;
    ImageUtil util = getWrapper(enrPhoto.getType(), null) ;
    if (util == null) return enrPhoto.getImagePath() ;
    
    util.shrink(enrPhoto.getImagePath(), fPhoto.toString(), width);
    fPhoto.deleteOnExit() ;

    return fPhoto.toString() ;
  }

  public static void exec (String[] cmd) {
    execPS(cmd) ;
  }

  
  private static Process execPS (String[] cmd) {
    try {
      log.info("exec: "+Arrays.toString(cmd));
      return Runtime.getRuntime().exec(cmd);
    } catch (Exception e) {
      e.printStackTrace() ;
      return null ;
    }
  }
  
  public static int execWaitFor(String[] cmd) {
    Process ps = execPS(cmd);
    if (ps == null) return -1 ;

    BufferedReader reader = new BufferedReader (new InputStreamReader (ps.getInputStream()));
    String str = null ;
    while (true) {
      try {
	while ((str = reader.readLine()) != null) log.info(str) ;
	
	reader = new BufferedReader (new InputStreamReader (ps.getErrorStream()));
	while ((str = reader.readLine()) != null) log.info("err - "+str) ;
	int ret = ps.waitFor() ;
	log.info("ret:"+ret);
	
	return ret ;

      } catch (InterruptedException e) {
      } catch (IOException e) {}
    }
  }


  public static boolean link(String source, File dest) {
    return 0 == execWaitFor(new String[] {"ln", "-s", source, dest.toString()});
  }

  public static void remove(String file) {
    exec(new String[] {"rm", file, "-rf"});
  }
}

