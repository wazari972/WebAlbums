/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.test;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.annotation.File.Access;
import net.wazari.libvfs.annotation.Link;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.SFile;
import net.wazari.libvfs.inteface.SLink;
import net.wazari.libvfs.vfs.Resolver;


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
    
    @Link
    @File(name="link")
    public MyLink link = new MyLink();

    @Override
    public void load() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static class SubFolder implements ADirectory {
        @File(name="test1.txt", access={Access.R, Access.W})
        public IFile desc = new Content("111111111111");
        
        @File(access={File.Access.R})
        public IFile tags = new Variable("zzzzzzzzzz");

        @Override
        public void load() throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    public static void main (String[] args) {
        Root root = new Root();
        net.wazari.libvfs.vfs.LibVFS.resolver = new Resolver(root);
        com.jnetfs.core.JnetFS.main(new String[]{"/home/kevin/vayrac/WebAlbums/WebAlbums3-FS/test"});
    }
}
class MyLink extends SLink {
    @Override
    public String getTarget() {
        return "/home/kevin/";
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