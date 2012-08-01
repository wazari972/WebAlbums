/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import java.util.LinkedList;
import java.util.List;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;

/**
 *
 * @author kevin
 */
@File
public class Root implements ADirectory {
    @Directory
    public List<Theme> themes = new LinkedList<Theme>();
    
    public Root() {
        for (String name : new String[]{"France", "Grenoble"}) {
            themes.add(new Theme(name));
        }
    }
}
