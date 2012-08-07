/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import java.util.List;
import net.wazari.libvfs.annotation.File.Access;

/**
 *
 * @author kevin
 */
public class SDirectory implements IDirectory {
    protected IDirectory parent = null;
    
    @Override
    public void setParent(IDirectory parent) {
        this.parent = parent;
    }
    
    @Override
    public long getTime() {
        return 1;
    }
    
    @Override
    public long getSize() {
        return 1 << 12;
    }
    
    @Override
    public String getShortname() {
        String name = parent.getShortname(this);
        
        if (name != null) {
            return name;
        } else {
            return "generic_dir";
        }
    }
    
    @Override
    public IDirectory getParent() {
        return parent;
    }
    
    @Override
    public boolean supports(long flags) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void incReference() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void decReference() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getHandle() {
       throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void release() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void open() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String getContent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List<IFile> listFiles() {
        throw new UnsupportedOperationException("Not supported yet."+this);
    }
    
    @Override
    public String getShortname(IFile file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Access[] getAccess() {
        return new  Access[]{Access.R};
    }

    @Override
    public Access[] getAccess(IFile file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void unlink() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rmdir() {
    }
    
    @Override
    public void mkdir(String name) {
    }

    @Override
    public void create(String name) throws Exception {
    }

    @Override
    public void rename(IDirectory targetDir, String filename) {
    }

    @Override
    public void addFile(IFile file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rmFile(IFile file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void touch() {
    }
}
