/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.exchange;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.wazari.service.exchange.Configuration;
import net.wazari.view.servlet.utils.XmlUtils;

/**
 *
 * @author pk033
 */
@XmlRootElement(name = "Configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationXML implements Configuration {
    private static final Logger log = Logger.getLogger(ConfigurationXML.class.getName());

    private static final String SEP = File.separator;
    private static String rootPath = "/photo" ;

    static {
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("RootPath.conf") ;
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = reader.readLine();
            if (line != null) rootPath = line ;
            else {
                log.log(Level.SEVERE, "Could not read RootPath from file: empty");
            }
        } catch (IOException ex) {
            log.log(Level.SEVERE, "Could not read RootPath from file: {0}", ex.getMessage());
        }
        log.log(Level.WARNING, "Root path retrieved: "+rootPath);
    }
    
    private static Configuration conf ;
    static {
        conf = new ConfigurationXML() ;
        
        File file = null ;
        try {
            file = new File(conf.getConfigFilePath()) ;
            conf = XmlUtils.reload(file , ConfigurationXML.class) ;
            log.log(Level.INFO, "Configuration correctly loaded from {0}", file.getAbsolutePath());
            log.info(XmlUtils.print((ConfigurationXML)conf, ConfigurationXML.class));
        } catch (Exception e) {
            log.log(Level.WARNING, "Exception while loading the Configuration from {1}", e.getCause().getMessage());
            log.log(Level.SEVERE, "Using default configuration ...");
        }
    }
    public static Configuration getConf() {
        return conf ;
    }
    public static void setConf(ConfigurationXML conf) {
        ConfigurationXML.conf = conf ;
    }

    @XmlAttribute
    private String date = new SimpleDateFormat("yyyy-mm-dd:HH-mm").format(new Date());
    @XmlElement
    private Directories directories = new Directories();
    @XmlElement
    private Sizes sizes = new Sizes();
    @XmlElement
    private Properties properties = new Properties();

    private ConfigurationXML(){}

    @Override
    public boolean hasInternet() {

        return properties.hasInternet;

    }

    @Override
    public boolean isReadOnly() {

        return properties.isReadOnly;

    }
    
    @Override
    public boolean wantAlightenDb() {

        return properties.alightenDb;

    }

    @Override
    public boolean wantStats() {

        return properties.wantStats;

    }

    @Override
    public boolean wantXsl() {

        return properties.wantXsl;

    }

    @Override
    public int getAlbumSize() {

        return sizes.albums;

    }

    @Override
    public int getPhotoSize() {

        return sizes.photos;

    }

    /** Pathes **/
    @Override
    public String getData() {

        return directories.data;

    }

    @Override
    public String getRootPath() {
        if (rootPath == null) {
            log.warning("Rootpath null ...");
            return null;
        }

        File rootDirFile = new File (rootPath) ;
        if (!rootDirFile.exists()) {
            log.warning("Rootpath doesn't exists ...");
            return null ;
        }
        if (!rootDirFile.isDirectory()) {
            log.warning("Rootpath is not a directory ...");
            return null ;
        }

        if (!rootDirFile.isAbsolute()) {
            try {
                rootPath = rootDirFile.getAbsoluteFile().getCanonicalPath();
            } catch (IOException ex) {
                log.warning("Couldn't unrelativize the path:" +ex.getMessage());
            }
        }

        return rootPath;

    }

    @Override
    public String getDataPath() {
        return getRootPath() + SEP + directories.data;
    }

    @Override
    public String getBackupPath() {
        return getDataPath() + SEP + directories.backup;
    }

    @Override
    public String getImagesPath() {
        return getDataPath() + SEP + directories.images;
    }

    @Override
    public String getFtpPath() {
        return getDataPath() + SEP + directories.ftp;

    }

    @Override
    public String getMiniPath() {
        return getDataPath() + SEP + directories.mini;
    }

    @Override
    public String getTempPath() {
        return getDataPath() + SEP + directories.temp;
    }
    
    @Override
    public String getConfigFilePath() {
        return getDataPath() + SEP + directories.confFile;
    }

    @Override
    public String getSep() {
        return SEP ;
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
    }

    private static class Sizes {

        @XmlElement
        private int albums = 15;
        @XmlElement
        private int photos = 15;
    }

    private static class Properties {

        @XmlElement
        private boolean hasInternet = true;
        @XmlElement
        private boolean isReadOnly = false;
        @XmlElement
        private boolean alightenDb = false;
        @XmlElement
        private boolean wantStats = false;
        @XmlElement
        private boolean wantXsl = true;
    }
}
