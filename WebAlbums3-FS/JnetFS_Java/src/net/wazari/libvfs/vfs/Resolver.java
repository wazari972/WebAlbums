/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.vfs;

import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.inteface.IDirectory;
import net.wazari.libvfs.inteface.IFile;
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
    
    private IntrosRoot root ;
    private final String pathPrefix;
    public Resolver(ADirectory rootDir, String pathPrefix) {
        root = new IntrosRoot(rootDir);
        this.pathPrefix = pathPrefix;
    }
    public IFile getFile(String search) {
        if (search.startsWith(pathPrefix)) {
            /* CHANGE THAT*/
            search = search.substring(pathPrefix.length() + 11);
            search = "/France/Albums"+search;
            log.warn("GET FILE: prefix > {}", search);
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
