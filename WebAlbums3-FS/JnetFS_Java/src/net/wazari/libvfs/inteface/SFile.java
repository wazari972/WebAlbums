/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;


/**
 *
 * @author kevin
 */
public abstract class SFile implements IFile {
    public int reference = 0;
    public int mtime = 1000 ;
    
    protected String content = "Generic content";
    
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
        return content.length();
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
    public String getShortname(IDirectory context) {
        return "generic_file" ;
    }
}
