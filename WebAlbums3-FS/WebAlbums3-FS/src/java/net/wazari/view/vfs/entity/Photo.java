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
    private String name;
    private String target;
    
    public Photo(XmlDetails details) {
        if (details == null)
            return;
        target = details.photoId.path;
        name = target.substring(target.lastIndexOf("/")+1);
    }
    
    @Override
    public String getTarget() {
        return "/home/kevin/vayrac/data/images/"+target;
    }
    
    @Override
    public String getShortname() {
        return name;
    }
}
