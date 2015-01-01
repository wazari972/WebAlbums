/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.exchange;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.*;
import net.wazari.common.util.XmlUtils;
import net.wazari.service.exchange.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author pk033
 */
@XmlRootElement(name = "Configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationXML implements Configuration {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationXML.class.getName());

    private static String SEP ;
    private static String rootPath  ;
    private static boolean isPathURL ;
    private static boolean inited = false;
    public static void init() {
        if (inited) {
            return;
        }
        inited = true;
        initRootPath();
        initConfiguration();
    }
    
    private static void initRootPath() {
        LinkedList<String> paths = new LinkedList<>() ;
        
        String prop = System.getProperty("root.path") ;
        if (prop != null) {
            log.info("Property 'root.path'  found, '{}' added on top of the list...", prop); 
            paths.addFirst(prop) ;
        }
            
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("RootPath.conf") ;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = reader.readLine();

            while (line != null) {
                log.debug("Path '{}' found in classpath://RootPath.conf", line);
                paths.addLast(line) ;
                line = reader.readLine();
            }
        } catch (IOException ex) {
            log.error( "Could not read RootPath from file: {}", ex.getMessage());
        }

        String thePath = null;
        for (String path : paths) {
            if (!path.startsWith("http://")) {
                File rootDirFile = new File (path) ;
                
                if (!rootDirFile.exists()) {
                    log.error("Rootpath '{}' doesn't exists ...", path);
                    continue;
                } else if (!rootDirFile.isDirectory()) {
                    log.error("Rootpath '{}' is not a directory ...");
                    continue;                    
                }

                if (!rootDirFile.isAbsolute()) {
                    try {
                        path = rootDirFile.getAbsoluteFile().getCanonicalPath();
                    } catch (IOException ex) {
                        log.warn( "Couldn''t unrelativize the path:{}", ex.getMessage());
                        continue;
                    }
                }
            }
            thePath = path;
            break;
            
        }
        if (thePath == null) {
            throw new IllegalArgumentException("Could not work out a valid root path ...") ;
        }
        
        isPathURL = thePath.startsWith("http://");
        SEP = isPathURL ? "/" : File.separator;
        if (!thePath.endsWith(SEP)) {
            thePath += SEP ;
        }
        rootPath = thePath;
        log.warn( "Root path retrieved: {}", rootPath);
    }
    
    private static Configuration conf ;
    private static void initConfiguration() {
        conf = new ConfigurationXML() ;
        
        try {
            InputStream is ;
            if (isPathURL) {
                is = new URL(conf.getConfigFilePath()).openStream() ;
            } else {
                is = new FileInputStream(new File(conf.getConfigFilePath())) ;
            }
            conf = XmlUtils.reload(is , ConfigurationXML.class) ;
            log.info( "Configuration correctly loaded from {}", conf.getConfigFilePath());
            log.info(XmlUtils.print((ConfigurationXML)conf, ConfigurationXML.class));
        } catch (IOException | JAXBException e) {
            log.warn( "Exception while loading the Configuration from {}", e);
            log.error( "Using default configuration ...");
        }
    }
    public static Configuration getConf() {
        return conf ;
    }
    public static void setConf(ConfigurationXML conf) {
        ConfigurationXML.conf = conf ;
    }

    @XmlAttribute
    private final String date = new SimpleDateFormat("yyyy-MM-dd:HH-mm").format(new Date());
    @XmlElement
    private final Directories directories = new Directories();
    @XmlElement
    private final Properties properties = new Properties();

    private ConfigurationXML(){}

    @Override
    public boolean isReadOnly() {
        return properties.isReadOnly || isPathURL ;
    }

    /* Paths */

    @Override
    public String getRootPath() {
        return rootPath;

    }

    public String getDataPath(boolean withRoot) {
        return (withRoot ? getRootPath() : "") + directories.data + SEP;
    }

    @Override
    public String getBackupPath() {
        return getDataPath(true) + directories.backup + SEP;
    }

    @Override
    public String getImagesPath(boolean withRoot) {
        return getDataPath(withRoot) + directories.images + SEP;
    }

    @Override
    public String getFtpPath() {
        return getDataPath(true) + directories.ftp + SEP;

    }

    @Override
    public String getMiniPath(boolean withRoot) {
        return getDataPath(withRoot) + directories.mini + SEP;
    }

    @Override
    public String getTempPath() {
        return getDataPath(true) + directories.temp + SEP;
    }

    @Override
    public String getPluginsPath() {
        return getDataPath(true) + directories.plugins + SEP;
    }
    
    @Override
    public String getConfigFilePath() {
        return getDataPath(true) + directories.confFile;
    }

    @Override
    public String getSep() {
        return SEP ;
    }

    @Override
    public boolean isPathURL() {
        return isPathURL ;
    }

    @Override
    public boolean wantsProtectDB() {
        return properties.protectDB ;
    }

    private static class Directories {
        @XmlElement
        private String data = "data";
        @XmlElement
        private String images = "images";
        @XmlElement
        private String mini = "miniatures";
        @XmlElement
        private String ftp = "ftp";
        @XmlElement
        private String temp = "tmp";
        @XmlElement
        private String backup = "backup";
        @XmlElement
        private String confFile = "conf/conf.xml";

        private String plugins = "plugins";
    }

    private static class Properties {

        @XmlElement
        private boolean isReadOnly = false;

        @XmlElement
        private boolean protectDB = true;
    }
}
