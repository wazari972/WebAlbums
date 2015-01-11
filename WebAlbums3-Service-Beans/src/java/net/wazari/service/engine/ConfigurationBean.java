/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
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

@Stateless
public class ConfigurationBean implements Configuration {
    final static String CONFIG_PATH = "conf/conf.xml";
    static final Logger log = LoggerFactory.getLogger(ConfigurationBean.class.getName());
    
    private static ConfigurationXML conf;

    @Override
    public Class getConfClass() {
        return conf.getClass();
    }

    @Override
    public Object getConf() {
        return conf;
    }

    @Override
    public void setConf(Object conf) {
        ConfigurationBean.conf = (ConfigurationXML) conf;
    }
    
    @Startup
    @Singleton
    public static class Initializer {
        @PostConstruct
        void init () {
            ConfigurationBean.conf = ConfigurationXML.init();
        }
    }
    
    @Override
    public boolean isReadOnly() {
        return conf.properties.isReadOnly || ConfigurationXML.isPathURL ;
    }

    /* Paths */

    @Override
    public String getRootPath() {
        return ConfigurationXML.rootPath;

    }

    @Override
    public String getBackupPath() {
        return getRootPath() + conf.directories.backup + getSep();
    }

    @Override
    public String getImagesPath(boolean withRoot) {
        return (withRoot? getRootPath(): "") + conf.directories.images + getSep();
    }

    @Override
    public String getFtpPath() {
        return getRootPath() + conf.directories.ftp + getSep();

    }

    @Override
    public String getMiniPath(boolean withRoot) {
        return (withRoot? getRootPath(): "") + conf.directories.mini + getSep();
    }

    @Override
    public String getTempPath() {
        return getRootPath() + conf.directories.temp + getSep();
    }

    @Override
    public String getPluginsPath() {
        return getRootPath() + conf.directories.plugins + getSep();
    }
    
    @Override
    public String getConfigFilePath() {
        return getRootPath() + ConfigurationBean.CONFIG_PATH;
    }

    @Override
    public String getSep() {
        return ConfigurationXML.SEP ;
    }

    @Override
    public boolean isPathURL() {
        return ConfigurationXML.isPathURL ;
    }

    @Override
    public boolean wantsProtectDB() {
        return conf.properties.protectDB ;
    }
    
}

@XmlRootElement(name = "Configuration")
@XmlAccessorType(XmlAccessType.FIELD)
class ConfigurationXML {
    static String SEP ;
    static String rootPath  ;
    static boolean isPathURL ;
    static ConfigurationXML inited = null;
    
    static ConfigurationXML init() {
        if (inited != null) {
            return inited;
        }
        initRootPath();
        inited = initConfiguration();
        
        return inited;
    }
    
    private static void initRootPath() {
        LinkedList<String> paths = new LinkedList<>() ;
        
        String prop = System.getProperty("root.path") ;
        if (prop != null) {
            ConfigurationBean.log.info("Property 'root.path'  found, '{}' added on top of the list...", prop); 
            paths.addFirst(prop) ;
        }
            
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("RootPath.conf") ;
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = reader.readLine();

                while (line != null) {
                    ConfigurationBean.log.debug("Path '{}' found in classpath://RootPath.conf", line);
                    paths.addLast(line) ;
                    line = reader.readLine();
                }
            } else {
                ConfigurationBean.log.warn( "Could not find RootPath.conf inside the classpath ...");
            }
        } catch (IOException ex) {
            ConfigurationBean.log.error( "Could not read RootPath from file: {}", ex.getMessage());
        }

        String thePath = null;
        for (String path : paths) {
            if (!path.startsWith("http://")) {
                File rootDirFile = new File (path) ;
                
                if (!rootDirFile.exists()) {
                    ConfigurationBean.log.error("Rootpath '{}' doesn't exists ...", path);
                    continue;
                } else if (!rootDirFile.isDirectory()) {
                    ConfigurationBean.log.error("Rootpath '{}' is not a directory ...");
                    continue;                    
                }

                if (!rootDirFile.isAbsolute()) {
                    try {
                        path = rootDirFile.getAbsoluteFile().getCanonicalPath();
                    } catch (IOException ex) {
                        ConfigurationBean.log.warn( "Couldn''t unrelativize the path:{}", ex.getMessage());
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
        ConfigurationBean.log.warn( "Root path retrieved: {}", rootPath);
    }
    
    private static ConfigurationXML initConfiguration() {
        ConfigurationXML conf;
        try {
            InputStream is ;
            String config_path = rootPath + ConfigurationBean.CONFIG_PATH;
            if (isPathURL) {
                is = new URL(config_path).openStream() ;
            } else {
                is = new FileInputStream(new File(config_path)) ;
            }
            
            conf = XmlUtils.reload(is , ConfigurationXML.class) ;
            ConfigurationBean.log.info("Configuration correctly loaded from {}", config_path);
        } catch (IOException | JAXBException e) {
            ConfigurationBean.log.warn("Exception while loading the Configuration from {}", e);
            ConfigurationBean.log.error("Using default configuration ...");
            
            conf = new ConfigurationXML();
        }
        try {
            ConfigurationBean.log.info(XmlUtils.print((ConfigurationXML) conf, ConfigurationXML.class));
        } catch (JAXBException ex) {
            ConfigurationBean.log.warn("Exception while printing the Configuration file {} ...", conf);
        }
        
        return conf;
    }

    @XmlAttribute
    final String date = new SimpleDateFormat("yyyy-MM-dd:HH-mm").format(new Date());
    @XmlElement
    final Directories directories = new Directories();
    @XmlElement
    final Properties properties = new Properties();

    private ConfigurationXML(){}

    static class Directories {
        @XmlElement
        String images = "images";
        @XmlElement
        String mini = "miniatures";
        @XmlElement
        String ftp = "ftp";
        @XmlElement
        String temp = "tmp";
        @XmlElement
        String backup = "backup";
        @XmlElement
        String plugins = "plugins";
    }
    
    public static class Properties {
        @XmlElement
        boolean isReadOnly = false;

        @XmlElement
        boolean protectDB = true;
        
        @XmlElement
        String automountWFS = null;
    }
}
