package net.wazari.common.constante;

import javax.servlet.ServletContext;

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.net.URI;
import java.awt.Desktop;

import java.util.logging.Logger;
import net.wazari.service.exchange.Configuration;

public class Path implements Configuration {

    private static final Logger log = Logger.getLogger(Path.class.toString());
    private static final String URL = "file://";
    private static final String DATA = "data";
    private static final String SEP = File.separator;

    private static Path conf = null ;
    public synchronized static Configuration getConf(ServletContext context) {
        if (conf == null) {
            conf = new Path() ;
            conf.init(context);
        }
        return conf ;
    }

    //forbid instanciation outside of the factory
    private Path () {}

    public String getSep() {
        return SEP;
    }
    private static final String TMP = DATA + SEP + "tmp";
    private static final String FTP = DATA + SEP + "ftp";
    private static final String IMAGES = DATA + SEP + "images";
    private static final String MINI = DATA + SEP + "miniatures";
    private Map<String, Boolean> boolParams = new HashMap<String, Boolean>();
    private Map<String, String> strParams = new HashMap<String, String>();
    private String home_dir = null;

    public void init(ServletContext context) {
        log.info("Initialization of the PATH options ...");
        initStrParam(context, "root_dir", "/photo/");

        //home_dir is used if root_dir is relative (./ or ../)
        home_dir = System.getProperty("user.dir");
        if ("\\".equals(File.separator)) {
            //on Windows, rewrite the path with / instead of	\
            //for URLs
            log.fine(File.separator);
            home_dir = home_dir.replace("\\", "/");
        }

        initBoolParam(context, "read_only", false);
        initBoolParam(context, "has_internet", true);
        initBoolParam(context, "wants_stats", false);
        initBoolParam(context, "wants_queries", false);
        initBoolParam(context, "wants_xsl", true);
        initBoolParam(context, "lighten_db", false);

        initStrParam(context, "sgbd", "hsqldb");

        initStrParam(context, "auto_login", "3");
        if (isReadOnly()) {
            if (Desktop.isDesktopSupported()) {
                if (Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                    try {
                        log.info("Starting the WebBrowser...");
                        java.awt.Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080/WebAlbums/Index"));
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }

    public void initBoolParam(ServletContext context,
            String param, boolean defaut) {
        boolean value = defaut;
        String strVal = context.getInitParameter(param);
        if (strVal != null) {
            value = "true".equals(strVal);
        }
        //log.debug("@"+param +" => "+value);

        boolParams.put(param, value);
    }

    public void initStrParam(ServletContext context,
            String param, String defaut) {
        String value = defaut;
        String strVal = context.getInitParameter(param);
        if (strVal != null) {
            value = strVal;
        }
        log.fine("@" + param + " => " + value);
        strParams.put(param, value);
    }

    public boolean isReadOnly() {
        return boolParams.get("read_only");
    }

    public boolean hasInternet() {
        return boolParams.get("has_internet");
    }

    public boolean wantsStats() {
        return boolParams.get("wants_stats");
    }

    public boolean wantsXsl() {
        return boolParams.get("wants_xsl");
    }

    public boolean wantsQueries() {
        return boolParams.get("wants_queries");
    }

    public boolean lightenDb() {
        return boolParams.get("lighten_db");
    }
    private int count = 0;

    public Integer autoLogin() {
        try {
            if (count == 2) {
                strParams.put("auto_login", null);
            } else {
                count++;
            }
            return new Integer(strParams.get("auto_login"));
        } catch (Exception e) {
            return null;
        }
    }

    public String getSourcePath() {
        String path = strParams.get("root_dir");
        if (path == null) {
            return null;
        }
        if (path.charAt(0) == '.') {
            path = home_dir + SEP + path;
        }
        return path;
    }

    public String getSourceURL() {
        return URL + getSourcePath();
    }

    public String getTempDir() {
        return getSourcePath() + TMP;
    }

    public String getImages() {
        return IMAGES;
    }

    public String getDataPath() {
        return getSourcePath() + DATA;
    }

    public String getData() {
        return DATA;
    }

    public String getFTP() {
        return FTP;
    }

    public String getMini() {
        return MINI;
    }

    public boolean isSgbdHsqldb() {
        return "hsqldb".equals(strParams.get("sgbd"));
    }

    public boolean isSgbdMysql() {
        return "mysql".equals(strParams.get("sgbd"));
    }

    public void updateBoolParam(String param, String value) {
        Boolean boolValue = boolParams.get(param);
        if (boolValue == null) {
            return;
        }
        if (value != null) {
            boolValue = "true".equals(value);
        }
        boolParams.put(param, boolValue);
    }

    public void updateStrParam(String param, String value) {
        String oldValue = strParams.get(param);
        if (oldValue == null) {
            return;
        }
        if (value != null) {
            strParams.put(param, value);
        }
    }

    private static final int ALBUM_SIZE = 15 ;
    public int getAlbumSize() {
        try {
            return Integer.parseInt(strParams.get("album_size"));
        } catch(NumberFormatException e) {
            return ALBUM_SIZE ;
        }
    }

    private static final int PHOTO_SIZE = 15 ;
    public int getPhotoSize() {
        try {
            return Integer.parseInt(strParams.get("photo_size"));
        } catch(NumberFormatException e) {
            return PHOTO_SIZE ;
        }
    }
}
