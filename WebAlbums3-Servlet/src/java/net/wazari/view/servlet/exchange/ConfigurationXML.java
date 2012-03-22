/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.exchange;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.Configuration;
import net.wazari.common.util.XmlUtils;

/**
 *
 * @author pk033
 */
@XmlRootElement(name = "Configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationXML implements Configuration {
    private static final Logger log = LoggerFactory.getLogger(ConfigurationXML.class.getName());

    private static final String SEP = File.separator;
    private static final String rootPath  ;
    private static final boolean isPathURL ;
    static {
        String path = null ;
        try {
            String prop = System.getProperty("root.path") ;
            if (prop != null) path = prop ;
            else {
                log.warn("No 'root.path' property found, trying local RootPath.conf ...") ;
                InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("RootPath.conf") ;
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = reader.readLine();
                if (line != null) path = line ;
                else {
                    log.error( "Could not read RootPath from file: empty");
                    throw new IllegalArgumentException("Could not read Rootpath from "+path+": empty") ;
                }
            }
        } catch (IOException ex) {
            log.error( "Could not read RootPath from file: {}", ex.getMessage());
        }

        if (path == null) {
            throw new IllegalArgumentException("Could not work out a valid root path ...") ;
        }
        if (!path.startsWith("http://")) {
            isPathURL = false ;
            File rootDirFile = new File (path) ;
            if (!rootDirFile.exists()) {
                log.error("Rootpath doesn't exists ...");
                throw new IllegalArgumentException("Rootpath "+path+" doesn't exists ...") ;

            } else if (!rootDirFile.isDirectory()) {
                log.error("Rootpath is not a directory ...");
                throw new IllegalArgumentException("Rootpath "+path+" is not a directory ...") ;

            }

            if (!rootDirFile.isAbsolute()) {
                try {
                    path = rootDirFile.getAbsoluteFile().getCanonicalPath();
                } catch (IOException ex) {
                    log.warn( "Couldn''t unrelativize the path:{}", ex.getMessage());
                }
            }
            if (!path.endsWith(SEP)) {
                path += SEP ;
            }
        } else {
            isPathURL = true ;
            if (!path.endsWith("/")) {
                path += "/" ;
            }
        }

            
        rootPath = path ;
        log.warn( "Root path retrieved: {}", rootPath);
    }
    
    private static Configuration conf ;
    static {
        conf = new ConfigurationXML() ;
        
        try {
            InputStream is = null ;
            if (isPathURL) {
                is = new URL(conf.getConfigFilePath()).openStream() ;
            } else {
                is = new FileInputStream(new File(conf.getConfigFilePath())) ;
            }
            conf = XmlUtils.reload(is , ConfigurationXML.class) ;
            log.info( "Configuration correctly loaded from {}", conf.getConfigFilePath());
            log.info(XmlUtils.print((ConfigurationXML)conf, ConfigurationXML.class));
        } catch (Exception e) {
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
    private String date = new SimpleDateFormat("yyyy-MM-dd:HH-mm").format(new Date());
    @XmlElement
    private Directories directories = new Directories();
    @XmlElement
    private Properties properties = new Properties();

    private ConfigurationXML(){}

    @Override
    public boolean isReadOnly() {
        return properties.isReadOnly || isPathURL ;
    }

    /** Paths **/

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
