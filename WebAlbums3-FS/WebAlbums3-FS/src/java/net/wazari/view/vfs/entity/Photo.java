/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import net.wazari.libvfs.inteface.SLink;
import net.wazari.service.exchange.xml.common.XmlDetails;

/**
 *
 * @author kevin
 */
public class Photo extends SLink {
    protected String name;
    protected String target;
    protected Integer id;
    
    protected boolean doCompletePath = true;
    protected boolean uniqName = false;
    protected boolean forceFile = false;
    
    public Photo(String path, String name, Integer id) {
        setTarget(path);
        this.name = name;
        this.id = id;
        
        this.forceFile = (this.id == null);
    }
    
    public Photo(XmlDetails details, String name) {
        setTarget(details.photoId.path);
        this.name = name;
        this.id = details.photoId.id;
    }
    
    public Photo(XmlDetails details, boolean uniq) {
        this(details);
        
        this.uniqName = uniq;
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
        String path = System.getProperty("root.path") ;
        if (path == null) {
            path = "/other/Web/" ;
        }
        if (doCompletePath) {
            return path+"data/images/"+target;
        } else {
            return target;
        }
    }
    
    @Override
    public String getShortname() {
        return (uniqName ? Integer.toString(id) + "-" : "") + name;
    }
}
