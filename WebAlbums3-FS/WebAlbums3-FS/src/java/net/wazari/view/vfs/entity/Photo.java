/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import net.wazari.libvfs.inteface.SLink;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.view.vfs.Launch;

/**
 *
 * @author kevin
 */
public class Photo extends SLink {
    protected String name;
    protected String target;
    protected Integer id;
    
    protected boolean doCompletePathed = true;
    protected boolean uniqName = false;
    protected boolean forceFile = false;
    
    public Photo(XmlDetails details, String name) {
        this(details);
        this.name = name;
    }
    
    public Photo(XmlDetails details, boolean uniq) {
        this(details);
        /* UNIQ_NAME disabled: makes drag-and-drop copy crash, because of
           the automatique rename */
        //this.uniqName = uniq;
    }
    
    public Photo(XmlDetails details) {
        this(details.photoId.path, details.photoId.id);
    }
    
    public Photo(String path, int id) {
        setTarget(path);
        this.id = id;
    }
    
    @Override
    public boolean forceFile() {
        return this.forceFile;
    }
    
    protected final void setTarget(String target) {
        this.forceFile = target == null;
        
        if (null == target) {
            return;
        }
        
        this.target = target;
        name = target.substring(target.lastIndexOf("/")+1);
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
