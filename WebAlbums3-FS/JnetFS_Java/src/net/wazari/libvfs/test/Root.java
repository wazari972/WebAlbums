/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.test;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.annotation.File.Access;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.SFile;

/**
 *
 * @author kevin
 */
@Directory
@File(access={Access.R, File.Access.W, File.Access.X})
public class Root extends SDirectory implements ADirectory {
    @File(name="r.txt", access={File.Access.R})
    public IFile desc = new Content("Description");
    
    @File(name="w.txt", access={File.Access.W})
    public IFile desc2 = new Content("Description");
    
    @File(name="x.txt", access={File.Access.X})
    public IFile tags = new Content("Tags");
    
    @File(name="rw.txt", access={File.Access.R, File.Access.W})
    public IFile rw = new Content("Tags");
    
    @File(name="rwx.txt", access={File.Access.R, File.Access.W, File.Access.X})
    public IFile rwx = new Content("Tags");
    
    @Directory
    @File(name="sub", access={File.Access.R, File.Access.W, File.Access.X})
    public SubFolder sub = new SubFolder();
    
    public static class SubFolder implements ADirectory {
        @File(name="test1.txt", access={Access.R, Access.W})
        public IFile desc = new Content("111111111111");
        
        @File(access={File.Access.R})
        public IFile tags = new Variable("zzzzzzzzzz");
    }
}
class Content extends SFile {
    public Content(String content) {
        this.content = content;
    }
}

class Variable extends SFile {
    public Variable(String content) {
        this.content = content;
    }
    public static int count = 0;
    
    @Override
    public void open() {}
    
    @Override
    public String getContent() {
        return content + "<>" + count;
    }
    
    @Override
    public String getShortname() {
        return "getShortname"+count;
    }
}