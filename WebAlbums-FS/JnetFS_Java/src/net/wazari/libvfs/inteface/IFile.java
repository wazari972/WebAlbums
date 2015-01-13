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
public interface IFile {
    String getContent();
    long getSize();
    File.Access[] getAccess();
    long getTime();
    
    String getShortname();

    boolean supports(long flags);

    void incReference();
    void decReference();

    long getHandle();

    void open();
    void release();
    void close();
    void unlink() throws Exception;
    
    void setParent(IDirectory parent);
    IDirectory getParent();

    void touch();
    void truncate();

    void write(String new_content);
}
