package constante;

import javax.servlet.Servlet;
import javax.servlet.ServletContext ;
 
import java.util.Map ;
import java.util.HashMap ;

import engine.WebPage;

public class Path {
  private static Boolean home = null ;
  
  private static final String URL = "file://" ;
  private static String FILE_PATH = null ;
 
  private static final String DATA = "data" ;
  
  public static final String SEP = "/" ;

  public static final String LOCATION = "/WebAlbums/" ;
  
  public static final String APLIC = "WebAlbums" ;
  public static final String TMP = "tmp" ;
  public static final String FTP = DATA+SEP+"ftp" ;
  public static final String IMAGES = DATA+SEP+"images" ;
  public static final String MINI = DATA+SEP+"miniatures" ;
    
    private static Map<String, Boolean> boolParams = new HashMap<String, Boolean>() ;
    private static Map<String, String> strParams = new HashMap<String, String>() ;
  
  public static void init (ServletContext context) {     
      FILE_PATH = context.getInitParameter("data_dir");
      
      initBoolParam(context, "read_only", false);
      initBoolParam(context, "has_internet", true);
      initBoolParam(context, "wants_stats", false);

      initStrParam(context, "data_dir", "./");
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
	
	strParams.put(param, strVal) ;
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

  public static String getSourcePath () {
      return strParams.get("data_dir");
  }
  
  public static String getSourceURL () {
    return URL+getSourcePath () ;
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
