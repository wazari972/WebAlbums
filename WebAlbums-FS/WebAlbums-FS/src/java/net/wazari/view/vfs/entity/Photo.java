/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import net.wazari.libvfs.inteface.SLink;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.view.vfs.Launch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Photo extends SLink {
    private static final Logger log = LoggerFactory.getLogger(Photo.class.getCanonicalName());
    
    protected String name;
    private String target;
    protected Integer id;
    
    protected boolean doCompletePathed = true;
    protected boolean uniqName = false;
    // should we read this.content or this.target ?
    protected boolean forceFile;
    
    final Root root;
    public Photo(Root root, XmlDetails details, String name) {
        this(root, details);
        this.name = name;
        
    }
    
    public Photo(Root root, XmlDetails details, boolean uniq) {
        this(root, details);
        /* UNIQ_NAME disabled: makes drag-and-drop copy crash, because of
           the automatique rename */
        //this.uniqName = uniq;
    }
    
    public Photo(Root root, XmlDetails details) {
        this(root, details.photoId.path, details.photoId.id);
    }
    
    public Photo(Root root, String path, int id) {
        this.root = root;
        this.forceFile = root.PHOTO_AS_FILE;
        setPhoto(path, id);
        
    }
    
    protected final void setPhoto(String path, Integer id) {
        setTarget(path, id);
        this.id = id;
        if (root.PHOTO_AS_FILE) {
            this.setJFile(new java.io.File( Launch.getFolderPrefix(true)+path));
        }
    }
    
    @Override
    public boolean forceFile() {
        return this.forceFile;
    }
    
    private void setTarget(String path, Integer id) {
        if (!root.PHOTO_AS_FILE) {
            this.forceFile = path == null;
        }
        
        if (null == path) {
            return;
        }
        
        this.target = path;
        this.name = path.substring(path.lastIndexOf("/")+1);
    }
    
    @Override
    public String getTarget() {
        return Launch.getFolderPrefix(true) + target;
    }
    
    @Override
    public String getShortname() {
        return (uniqName ? Integer.toString(id) + "-" : "") + name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "File[Photo#"+id+"/"+name+"]";
    }
}
