/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.io.InputStream;
import java.util.Properties;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.SFile;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.libvfs.inteface.ValueFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Configuration  extends SDirectory implements ADirectory {
    private static final Logger log = LoggerFactory.getLogger(SFile.class.getCanonicalName()) ;
    
    @File(name="stars")
    public ValueFile stars;
    
    @File(name="image_access")
    public ValueFile imgAccess;
    
    @File(name="reload")
    public ValueFile reload;
    
    @File(name="version")
    public ValueFile fversion;
    
    public final Root root;
    
    public Configuration(Root root) {
        this.root = root;
        
        stars = new StarConfiguration();
        imgAccess = new ImageAccessConfiguration();
        reload = new ReloadConfiguration();
        fversion = new VersionConfiguration();
    }
    
    @Override
    public void load() throws VFSException {       
        //nothing here, the configuration has to be loaded only once.
    }
    
    class VersionConfiguration extends ValueFile {
        @Override
        public String getContent() {
            return Configuration.version;
        }
    }
    
    class ReloadConfiguration extends ValueFile {
        ReloadConfiguration() {
            this.content = "#delete me to reload the filesystem content";
        }
        @Override
        public void unlink() throws Exception{
            Configuration.this.root.changed = true;
        }
    }
    
    class StarConfiguration extends ValueFile {
        @Override
        public String getContent() {
            return Integer.toString(Configuration.this.root.stars);
        }
        
        @Override
        public void setContent(String content) {
            try {
                Configuration.this.root.stars = Integer.parseInt(content.trim());
                Configuration.this.root.changed = true;
            } catch(NumberFormatException e) {
                log.info("Coudln't parse a number in '{}'", content);
            } catch (Exception ex) {
                log.warn(ex.getMessage());
            }
        }
    }
    class ImageAccessConfiguration extends ValueFile {
        private static final String ACCESS_LINK = "link";
        private static final String ACCESS_FILE = "file";
        
        private String access = Configuration.this.root.PHOTO_AS_FILE ? ACCESS_FILE : ACCESS_LINK;
        
        @Override
        public String getContent() {
            return this.access + " # valid values: "+ACCESS_LINK+" or "+ACCESS_FILE+"\n" ;
        }
        
        @Override
        public void setContent(String content) {
            if (content == null) {
                return;
            }
            if (content.contains("\n")) {
                content = content.split("\n")[0];
            }
            if (content.contains("#")) {
                content = content.split("#")[0];
            }
            content = content.trim();
            
            if (content.equals(ACCESS_LINK)) {
                Configuration.log.info("Switch image access to link.");
                
                Configuration.this.root.PHOTO_AS_FILE = false;
                Configuration.this.root.changed = true;
                
                this.access = content;
            } else if (content.equals(ACCESS_FILE)) {
                Configuration.log.info("Switch image access to file.");
                
                Configuration.this.root.PHOTO_AS_FILE = true;
                Configuration.this.root.changed = true;
                
                this.access = content;
            } else if (content.equals(access)) {
                Configuration.log.debug("Same content saved, nothing to change.");
            } else {
                if (!content.isEmpty()) {
                    Configuration.log.info("Imag file access mode unknown ({}), nothing to change.", content);
                }
            }
        }
        
        @Override
        public void unlink() {
            if (access.equals(ACCESS_LINK)) {
                this.setContent(ACCESS_FILE);
            } else {
                this.setContent(ACCESS_LINK);
            }
        }
    }
    
    public static final String version;
    static {
        String ver;
        try {
            Properties version_props = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream("META-INF/version.properties");
            version_props.load(stream);
            
            ver = version_props.getProperty("git-tag") + "." +
                  version_props.getProperty("git-count");
            
            if ("True".equals(version_props.getProperty("dev-release"))) {
                ver += "-git";
                
                if ("True".equals(version_props.getProperty("pkg-release"))) {
                    ver += "."+version_props.getProperty("git-date");
                }
            }
            
        } catch (Exception ex) {
            log.warn("Couln't initialize version property file ... {}", ex.getMessage(), ex);
            ver = "unknown-git";
        }
        
        version = ver;
    }
}
