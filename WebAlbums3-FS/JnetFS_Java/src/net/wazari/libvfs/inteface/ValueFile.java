/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import net.wazari.libvfs.annotation.File;

/**
 *
 * @author kevin
 */
public abstract class ValueFile extends SFile {    
    @Override
    public String getContent() {
        /* Override me !*/
        return super.getContent();
    }
    
    public void setContent(String content) {
        /* Override me !*/
    }
    
    @Override
    public void unlink() throws Exception{
        /* Override me !*/
        super.unlink();
    }
    
    @Override
    public void open() {
        //currently, open doesn't check of a O_CREAT / O_APPEND flag ...
        this.truncate();
    }
    
    @Override
    public void close() {
        this.setContent(this.content);
    }
    
    @Override
    public File.Access[] getAccess() {
        return new File.Access[]{File.Access.R, File.Access.W};
    }
}
