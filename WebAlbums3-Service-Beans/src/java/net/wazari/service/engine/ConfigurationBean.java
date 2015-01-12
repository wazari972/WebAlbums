/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
        return ConfigurationXML.configPath;
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
    
    @Override
    public String getAutomountWFS() {
        if (conf.WFS.automount.enabled != null && !conf.WFS.automount.enabled) {
            return null;
        }
        return conf.WFS.automount.path;
    }
}

@XmlRootElement(name = "Configuration")
@XmlAccessorType(XmlAccessType.FIELD)
class ConfigurationXML {
    static String SEP ;    
    static boolean isPathURL = false;
    static String rootPath = null;
    static ConfigurationXML inited = null;
    
    static ConfigurationXML init() {
        if (inited != null) {
            return inited;
        }
        inited = initConfiguration();
        
        return inited;
    }
    static String configPath;
    private static final String CONFIG_PATH_PROPNAME = "config.path";
    private static void initConfigPath() {
        LinkedList<String> paths = new LinkedList<>() ;
        
        String prop = System.getProperty(CONFIG_PATH_PROPNAME) ;
        if (prop != null) {
            ConfigurationBean.log.info("Property '{}' found, '{}' added on top of the list.", CONFIG_PATH_PROPNAME, prop); 
            paths.addFirst(prop) ;
        }
            
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_PATH_PROPNAME) ;
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = reader.readLine();

                while (line != null) {
                    ConfigurationBean.log.debug("Config path '{}' found in classpath://{}", line);
                    paths.addLast(line) ;
                    line = reader.readLine();
                }
            } else {
                ConfigurationBean.log.warn( "Could not find ConfigPath.conf inside the classpath ...");
            }
        } catch (IOException ex) {
            ConfigurationBean.log.error( "Could not read {} from file: {}", new Object[]{CONFIG_PATH_PROPNAME, ex.getMessage(), ex});
        }

        for (String path : paths) {
            if (!path.startsWith("http://")) {
                File confDirFile = new File (path) ;
                
                if (!confDirFile.exists()) {
                    ConfigurationBean.log.error("Configation path '{}' doesn't exists ...", path);
                    continue;
                } else if (!confDirFile.isFile()) {
                    ConfigurationBean.log.error("Configation path '{}' is not a file ...", path);
                    continue;                    
                } else if (!confDirFile.canRead()) {
                    ConfigurationBean.log.error("Configation path '{}' cannot be read ...", path);
                    continue;                    
                }
            }
            configPath = path;
            break;
        }
        
        if (configPath == null) {
            throw new IllegalArgumentException("Could not work out a valid root path ...") ;
        }
    }
    
    private static ConfigurationXML initConfiguration() {
        initConfigPath();
        ConfigurationXML conf;
        try {
            InputStream is = new FileInputStream(new File(configPath)) ;
            conf = XmlUtils.reload(is, ConfigurationXML.class) ;
            
            ConfigurationBean.log.info("Configuration correctly loaded from {}", configPath);
        } catch (IOException | JAXBException e) {
            ConfigurationBean.log.warn("Exception while loading the Configuration from {}", e.getMessage(), e);
            ConfigurationBean.log.error("Using default configuration ...");
            
            conf = new ConfigurationXML();
        }
        
        try {
            ConfigurationBean.log.info(XmlUtils.print((ConfigurationXML) conf, ConfigurationXML.class));
        } catch (JAXBException ex) {
            ConfigurationBean.log.warn("Exception while printing the Configuration file {} ... ({})", new Object[]{conf, ex.getMessage(), ex});
        }
        
        String thePath = null;
        for (String path : conf.directories.root_path) {
            if (!path.startsWith("http://")) {
                File rootDirFile = new File(path);
                
                if (!rootDirFile.exists()) {
                    ConfigurationBean.log.info("Root path '{}' doesn't exists ...", path);
                    continue;
                } else if (!rootDirFile.isDirectory()) {
                    ConfigurationBean.log.info("Root path '{}' is not a directory ...");
                    continue;
                }

                path = rootDirFile.getAbsolutePath();
            } else {
                isPathURL = true;
            }
            thePath = path;
            break;
        }
        
        if (thePath == null) {
            throw new IllegalArgumentException("Could not work out a valid root path ...") ;
        }
        
        SEP = isPathURL ? "/" : File.separator;
        
        if (!thePath.endsWith(SEP)) {
            thePath += SEP ;
        }
        rootPath = thePath;
        ConfigurationBean.log.warn("Root path set to '{}'", rootPath);
        
        return conf;
    }
    
    @XmlAttribute
    final String date = new SimpleDateFormat("yyyy-MM-dd:HH-mm").format(new Date());
    @XmlElement
    final Directories directories = new Directories();
    @XmlElement
    final Properties properties = new Properties();
    @XmlElement
    final WFSProperties WFS = new WFSProperties();
    
    private ConfigurationXML(){}

    static class Directories {
        @XmlElement
        List<String> root_path = new LinkedList<>(Arrays.asList(new String[]{"/path/to/web/albums/data"}));
        
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
    }
    
    public static class WFSProperties {
        @XmlAttribute
        Boolean enabled = null;
        
        @XmlElement
        final Automount automount = new Automount();
        
        public static class Automount {
            @XmlValue
            String path = "/path/to/automount/wfs";
            
            @XmlAttribute
            Boolean enabled = false;
        }
    }
}
