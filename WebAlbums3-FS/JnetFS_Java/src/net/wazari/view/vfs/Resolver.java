/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import java.util.Map;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.IntrosDirectory;
import net.wazari.libvfs.test.Root;

/**
 *
 * @author kevin
 */
public class Resolver {
    public static IFile getFile(String search) {
        IntrosDirectory start = new IntrosDirectory(new Root(), null);
        
        if (search.equals("/")) {
            return start;
        }
        
        return getFile(start, "", search);
    }
    
    public static IFile getFile(IntrosDirectory current, String path, String search) {
        Map<String, IFile> map = current.listFiles();
        for (String fname : map.keySet()) {
            IFile file = map.get(fname);
            String fullname = path + "/" + fname ;
            
            if (search.equals(fullname)) {
                return file;
            }
            
            if (file instanceof IntrosDirectory) {
                if (search.startsWith(fullname)) {
                    return (IFile) getFile((IntrosDirectory) file, fullname, search);
                }
                
            } else if (file instanceof ADirectory) {
                if (search.startsWith(fullname)) {
                    return (IFile) getFile(new IntrosDirectory((ADirectory) file, fname), fullname, search);
                }
                
            } else {
                //nothing to do here, wrong file
            } 
        }
        
        return null;
    }  
}
