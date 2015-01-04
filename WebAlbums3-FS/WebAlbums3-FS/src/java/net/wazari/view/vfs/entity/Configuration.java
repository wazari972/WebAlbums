/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.SFile;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.libvfs.inteface.ValueFile;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Configuration  extends SDirectory implements ADirectory {
    
    private static final Logger log = LoggerFactory.getLogger(SFile.class.getCanonicalName()) ;
    public static boolean PHOTO_AS_FILE = false;
    
    @File(name="stars")
    public ValueFile stars;
    
    @File(name="image_access")
    public ValueFile imgAccess;
    
    final Root root;
    
    public Configuration(Root root) {
        this.root = root;
        
        stars = new StarConfiguration();
        imgAccess = new ImageAccessConfiguration();
    }
    
    @Override
    public void load() throws VFSException {       
        //nothing here, the configuration has to be loaded only once.
    }
    
    class StarConfiguration extends ValueFile {
        @Override
        public String getContent() {
            return Integer.toString(Session.stars);
        }
        
        @Override
        public void setContent(String content) {
            try {
                Session.stars = Integer.parseInt(content.trim());
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
        
        private String access = PHOTO_AS_FILE ? ACCESS_FILE : ACCESS_LINK;
        
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
                
                Configuration.PHOTO_AS_FILE = false;
                Configuration.this.root.changed = true;
                
                this.access = content;
            } else if (content.equals(ACCESS_FILE)) {
                Configuration.log.info("Switch image access to file.");
                
                Configuration.PHOTO_AS_FILE = true;
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
}
