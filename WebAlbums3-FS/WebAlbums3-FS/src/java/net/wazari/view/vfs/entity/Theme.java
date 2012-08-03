/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;

/**
 *
 * @author kevin
 */
public class Theme extends SDirectory implements ADirectory {
    @Directory
    @File(name="Albums")
    public final Albums albums = new Albums(this);
    @Directory
    @File(name="Tags")
    public final Tags tags = new Tags(this);
    @Directory
    @File(name="Carnets")
    public final Carnets carnets = new Carnets(this);
    
    @Directory
    @File(name="Geolocalizations")
    public final Geolocalizations geoloc = new Geolocalizations(this);
    
    private String name;
    
    public Theme(String name) {
        
    }

    Theme(String name, Launch aThis) {
        this.name = name;
        aThis.tagService.treatTagDISPLAY(new Session("") , null);
    }
    
    @Override
    public String getShortname() {
        return name;
    }
    
}
