/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.test;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.SFile;


/**
 *
 * @author kevin
 */
@Directory(name="Riit")
public class Root implements ADirectory{
    @File(name="desc.txt")
    public IFile desc = new Content("Description");
    @File(name="tags.txt")
    public IFile tags = new Content("Tags");
    
    @Directory(name="sub")
    public SubFolder sub = new SubFolder();
   
    
    public static class SubFolder implements ADirectory {
        @File(name="test1.txt")
        public IFile desc = new Content("111111111111");
        @File(name="test2.txt")
        public IFile tags = new Content("222222222222");
    }
}

class Content extends SFile {
    public Content(String content) {
        this.content = content;
    }
}