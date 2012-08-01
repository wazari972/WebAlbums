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
}
