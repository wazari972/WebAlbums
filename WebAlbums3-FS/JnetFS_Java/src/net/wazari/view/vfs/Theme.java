/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;

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
    public final Albums tags = new Albums(this);
    @Directory
    @File(name="Carnets")
    public final Carnets carnets = new Carnets(this);
    
    @Directory
    @File(name="Geolocalizations")
    public final Geolocalizations geoloc = new Geolocalizations(this);
    
    private String name;
    
    public Theme(String name) {
        this.name = name;
    }
    
    @Override
    public String getShortname() {
        return name;
    }
    
}
