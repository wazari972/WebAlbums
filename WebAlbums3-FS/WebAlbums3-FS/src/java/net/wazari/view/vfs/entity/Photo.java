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
        setTarget(details.photoId.path);
        this.name = name;
        this.id = details.photoId.id;
    }
    
    public Photo(XmlDetails details, boolean uniq) {
        this(details);
        /* UNIQ_NAME disabled: makes drag-and-drop copy crash, because of
           the automatique rename */
        //this.uniqName = uniq;
    }
    
    public Photo(XmlDetails details) {
        setTarget(details.photoId.path);
        this.id = details.photoId.id;
    }
    
    @Override
    public boolean forceFile() {
        return this.forceFile;
    }
    
    protected final void setTarget(String target) {
        this.forceFile = (target == null);
        
        if (this.forceFile) {
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
}
