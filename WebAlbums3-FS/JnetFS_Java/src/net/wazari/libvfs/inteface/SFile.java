/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import net.wazari.libvfs.annotation.File.Access;


/**
 *
 * @author kevin
 */
public class SFile implements IFile {
    public int reference = 0;    
    protected String content = "Generic content";
    protected IDirectory parent = null;
    
    @Override
    public boolean supports(long flags) {
        return true;
        //return flags == O_READONLY;
    }

    @Override
    public void incReference() {
        this.reference++ ;
    }

    @Override
    public void decReference() {
        this.reference-- ;
    }

    @Override
    public long getHandle() {
        return 1000;
    }
    
        @Override
    public String getContent() {
        return content;
    }
    
    @Override
    public long getSize() {
        return getContent().length();
    }
    
    @Override
    public long getTime() {
        return 10;
    }

    @Override
    public void release() {
        
    }

    @Override
    public void close() {
        
    }
    
    @Override
    public void open() {}
    
    @Override
    public String getShortname() {
        String name = parent.getShortname(this);
        
        if (name != null) {
            return name;
        } else {
            return "generic_file";
        }
    }

    @Override
    public Access[] getAccess() {
        if (getParent() != null) {
            return getParent().getAccess(this);
        } else {
            return new Access[]{Access.R};
        }
    }
    
    @Override
    public IDirectory getParent() {
        return parent;
    }

    @Override
    public void setParent(IDirectory parent) {
        this.parent = parent;
    }
}
