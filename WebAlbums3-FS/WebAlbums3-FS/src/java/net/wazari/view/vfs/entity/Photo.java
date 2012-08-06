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
    protected int id;
    
    protected boolean doCompletePath = true;
    protected boolean uniqName = false;
    
    public Photo(String path, String name, int id) {
        setTarget(path);
        this.name = name;
        this.id = id;
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
    
    protected final void setTarget(String target) {
        this.target = target;
        name = target.substring(target.lastIndexOf("/")+1);
    }
    
    @Override
    public String getTarget() {
        if (doCompletePath) {
            return "/home/kevin/vayrac/data/images/"+target;
        } else {
            return target;
        }
    }
    
    @Override
    public String getShortname() {
        return (uniqName ? Integer.toString(id) + "-" : "") + name;
    }
}
