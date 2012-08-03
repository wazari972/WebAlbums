/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.inteface.SDirectory;

/**
 *
 * @author kevin
 */
public class Tag extends SDirectory implements ADirectory {
    private String name ;
    public Tag(String name) {
        this.name = name;
    }
    
    @Override
    public String getShortname() {
        return name;
    }
    
}
