/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

/**
 *
 * @author kevin
 */
public interface IFile {
    String getContent();
    long getSize();
    long getTime();
    
    String getShortname(IDirectory context);

    boolean supports(long flags);

    void incReference();
    void decReference();

    long getHandle();

    void release();

    void close();
}
