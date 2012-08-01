/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.vfs;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.IntrosDirectory;
import net.wazari.libvfs.inteface.IntrosDirectory.IntrosRoot;

/**
 *
 * @author kevin
 */
public class Resolver {
    
    private IntrosRoot root ;
    public Resolver(ADirectory rootDir) {
        root = new IntrosRoot(rootDir);
    }
    public IFile getFile(String search) {
        if (search.equals("/")) {
            return root;
        }
        
        return getFile(root, "", search);
    }
    
    public IFile getFile(IntrosDirectory current, String path, String search) {
        for (IFile file : current.listFiles())  {
            file.setParent(current);
                
            String fullname = path + "/" + file.getShortname();
            
            if (search.equals(fullname)) {
                return file;
            }
            
            if (file instanceof IntrosDirectory) {
                if (search.startsWith(fullname)) {
                    return (IFile) getFile((IntrosDirectory) file, fullname, search);
                }
                
            } else if (file instanceof ADirectory) {
                if (search.startsWith(fullname)) {
                    return (IFile) getFile(new IntrosDirectory(current, (ADirectory) file), fullname, search);
                }
                
            } else {
                //nothing to do here, wrong file
            } 
        }
        
        return null;
    }  
}
