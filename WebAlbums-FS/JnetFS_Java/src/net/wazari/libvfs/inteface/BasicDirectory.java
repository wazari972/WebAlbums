/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import java.util.LinkedList;
import java.util.List;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;

/**
 *
 * @author kevin
 */
public class BasicDirectory extends SDirectory implements ADirectory {
    private final String name;
    @File
    public final List<IFile> files = new LinkedList<>();
    @Directory
    @File
    public final List<IDirectory> dirs = new LinkedList<>();
    
    public BasicDirectory(String name) {
        this.name = name;
    }
    
    /**
     *
     * @param file
     */
    public void addFileInside(IFile file) {
        files.add(file);
    }
    
    public void addDirInside(IDirectory file) {
        dirs.add(file);
    }
    
    @Override
    public String getShortname() {
        return this.name;
    }

    @Override
    public void load() throws VFSException {
        
    }
}
