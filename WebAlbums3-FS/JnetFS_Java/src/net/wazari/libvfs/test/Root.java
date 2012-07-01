/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.test;

import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.RootDirectory;
import net.wazari.libvfs.inteface.IFile;

/**
 *
 * @author kevin
 */
@RootDirectory(name="Riit")
public class Root {
    @File(name="desc.txt")
    public Content desc = new Content("Description");
    @File(name="tags.txt")
    public Content tags = new Content("Tags");
    
    @Directory(name="sub")
    public SubFolder sub = new SubFolder();
}


class Content implements IFile {
    private String content;
    public Content(String content) {
        this.content = content;
    }
    public String getContent() {
        return this.content;
    }
}

class SubFolder {
    @File(name="test1.txt")
    public Content desc = new Content("111111111111");
    @File(name="test2.txt")
    public Content tags = new Content("222222222222");
}