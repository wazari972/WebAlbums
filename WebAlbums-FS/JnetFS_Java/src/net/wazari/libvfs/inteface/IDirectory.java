/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import java.util.List;
import net.wazari.libvfs.annotation.File;

/**
 *
 * @author kevin
 */
public interface IDirectory extends IFile {
    List<IFile> listFiles() ;
    
    File.Access[] getAccess(IFile file);
    
    String getShortname(IFile file);

    void rmdir();
    
    void mkdir(String name);
    
    void create(String name) throws Exception ;
    
    void addFile(IFile file);
    void rmFile(IFile file);

    void moveIn(IFile srcFile, String filename) throws VFSException;
    void acceptNewFile(IFile file, String filename) throws VFSException;
}
