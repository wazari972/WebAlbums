package constante;

import javax.servlet.ServletContext ;
 
import java.util.Map ;
import java.util.HashMap ;
import java.io.File ;

import engine.WebPage ;

public class Path {  
  private static final String URL = "file://" ;
 
  public static final String DATA = "data" ;
  
  public static final String SEP = File.separator ;

  public static final String LOCATION = "/WebAlbums/" ;
  
  public static final String APLIC = "WebAlbums" ;
  public static final String TMP = "tmp" ;
  public static final String FTP = DATA+SEP+"ftp" ;
  public static final String IMAGES = DATA+SEP+"images" ;
  public static final String MINI = DATA+SEP+"miniatures" ;
    
  private static Map<String, Boolean> boolParams = new HashMap<String, Boolean>() ;
  private static Map<String, String> strParams = new HashMap<String, String>() ;

  private static String home_dir  = null ;
  
  public static void init (ServletContext context) {     
      initBoolParam(context, "read_only", false);
      initBoolParam(context, "has_internet", true);
      initBoolParam(context, "wants_stats", false);
      initBoolParam(context, "wants_queries", false);
      initBoolParam(context, "lighten_db", false);

      initStrParam(context, "root_dir", "../");
      initStrParam(context, "sgbd", "hsqldb");

      //URLiser le home_dir ss windows 
      home_dir = System.getProperty("user.dir") ;
  }
  
  public static void initBoolParam (ServletContext context,
				    String param, boolean defaut) {
    boolean value = defaut ;
    String strVal = context.getInitParameter(param) ;
    if (strVal != null) value = "true".equals(strVal) ;

    WebPage.log.debug("## Initialisation "+param+"@"+value);
    boolParams.put(param, value) ;
  }

  public static void initStrParam (ServletContext context,
				   String param, String defaut) {
    String value = defaut ;
    String strVal = context.getInitParameter(param) ;
    if (strVal != null) value = strVal ;
    
    WebPage.log.debug("## Initialisation "+param+"@"+value);  
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

  public static boolean wantsQueries() {
    return boolParams.get("wants_queries");
  }

  public static boolean lightenDb() {
    return boolParams.get("lighten_db");
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
