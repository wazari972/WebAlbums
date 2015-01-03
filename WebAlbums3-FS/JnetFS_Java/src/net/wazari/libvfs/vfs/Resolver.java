/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.vfs;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.inteface.IDirectory;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.IResolver;
import net.wazari.libvfs.inteface.IntrosDirectory;
import net.wazari.libvfs.inteface.IntrosDirectory.IntrosRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Resolver {
    private static final Logger log = LoggerFactory.getLogger(LibVFS.class.getCanonicalName());
    
    private final IntrosRoot root ;
    private final String externalPrefix;
    private final IResolver external;
    
    public Resolver(ADirectory rootDir, String externalPrefix, IResolver external) {
        root = new IntrosRoot(rootDir);
        this.externalPrefix = externalPrefix;
        this.external = external;
    }
    
    public IFile getFile(String search) {
        if (external != null && search.startsWith(externalPrefix)) {
            search = search.substring(externalPrefix.length());
            IFile found = external.getFile(search);
            log.warn("GET EXTERNAL FILE: {} > {}", search, found);
            return found;
        }
        
        if (search.equals("/") || search.equals("")) {
            return root;
        }
        
        return getFile(root, "", search);
    }
    
    public IFile getFile(IDirectory current, String path, String search) {
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
