package constante;

import javax.servlet.ServletContext ;
 
import java.util.Map ;
import java.util.HashMap ;
import java.io.File ;
import java.net.URI ;
import java.awt.Desktop ;

import engine.WebPage ;

public class Path {  
  private static final String URL = "file://" ;
 
  private static final String DATA = "data" ;
  
  public static final String SEP = File.separator ;

  private static final String TMP = DATA+SEP+"tmp" ;
  private static final String FTP = DATA+SEP+"ftp" ;
  private static final String IMAGES = DATA+SEP+"images" ;
  private static final String MINI = DATA+SEP+"miniatures" ;
    
  private static Map<String, Boolean> boolParams = new HashMap<String, Boolean>() ;
  private static Map<String, String> strParams = new HashMap<String, String>() ;

  private static String home_dir  = null ;
  
  public static void init (ServletContext context) {
    initStrParam(context, "root_dir", "../");
    
    //home_dir is used if root_dir is relative (./ or ../)
    home_dir = System.getProperty("user.dir") ;
    if ("\\".equals(File.separator)) {
      //on Windows, rewrite the path with / instead of	\
      //for URLs
      WebPage.log.debug(File.separator);
      home_dir = home_dir.replace("\\","/");
    }

    initBoolParam(context, "read_only", false);
    initBoolParam(context, "has_internet", true);
    initBoolParam(context, "wants_stats", false);
    initBoolParam(context, "wants_queries", false);
    initBoolParam(context, "wants_xsl", true);
    initBoolParam(context, "lighten_db", false);
    
    initStrParam(context, "sgbd", "hsqldb");
    
    initStrParam(context, "auto_login", "3");
    WebPage.log.info("starting up...");
    if (isReadOnly()) {
      if(Desktop.isDesktopSupported()){
	if(Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)){
	  try {
	    java.awt.Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080/WebAlbums/Index"));
	  } catch (Exception ex) {}
	}
      }
    }
  }
  
  public static void initBoolParam (ServletContext context,
				    String param, boolean defaut) {
    boolean value = defaut ;
    String strVal = context.getInitParameter(param) ;
    if (strVal != null) value = "true".equals(strVal) ;

    boolParams.put(param, value) ;
  }

  public static void initStrParam (ServletContext context,
				   String param, String defaut) {
    String value = defaut ;
    String strVal = context.getInitParameter(param) ;
    if (strVal != null) value = strVal ;
    
    strParams.put(param, value) ;
  }
  public static boolean isReadOnly() {
    return boolParams.get("read_only");
  }

  public static boolean hasInternet() {
    return boolParams.get("has_internet");
  }

  public static boolean wantsStats() {
    return boolParams.get("wants_stats");
  }

  public static boolean wantsXsl() {
    return boolParams.get("wants_xsl");
  }

  public static boolean wantsQueries() {
    return boolParams.get("wants_queries");
  }

  public static boolean lightenDb() {
    return boolParams.get("lighten_db");
  }

  private static int count = 0 ;
  public static String autoLogin() {
    try {
      if (count == 2) strParams.put("auto_login", null);
      else count ++ ;
      return strParams.get("auto_login") ;
    } catch (Exception e) {return null ;}
  }

  public static String getSourcePath () {
    String path = strParams.get("root_dir") ;
    if (path == null) return null ;
    if (path.charAt(0) == '.') {
      path = home_dir + SEP + path ;
    }
    return path ;
  }
  
  public static String getSourceURL () {
    return URL+getSourcePath () ;
  }
  
  public static String getTempDir () {
    return getSourcePath ()+TMP ;
  }

  public static String getImages () {
    return IMAGES ;
  }
  public static String getDataPath () {
    return getSourcePath()+DATA ;
  }
  public static String getData () {
    return DATA ;
  }
  public static String getFTP () {
    return FTP ;
  }
  public static String getMini () {
    return MINI ;
  }
  
  public static boolean isSgbdHsqldb () {
    return "hsqldb".equals(strParams.get("sgbd"));
  }

  public static boolean isSgbdMysql () {
    return "mysql".equals(strParams.get("sgbd"));
  }

  public static void updateBoolParam (String param, String value) {
    Boolean boolValue = boolParams.get(param);
    if (boolValue == null) return ;
    if (value != null) boolValue = "true".equals(value) ;	
    boolParams.put(param, boolValue) ;
  }
  public static void updateStrParam (String param, String value) {
    String oldValue = strParams.get(param);
    if (oldValue == null) return ;
    if (value != null) strParams.put(param, value) ;
  }
}
