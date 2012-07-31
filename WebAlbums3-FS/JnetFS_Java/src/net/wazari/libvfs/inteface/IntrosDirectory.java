/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import com.jnetfs.core.Code;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;

/**
 *
 * @author kevin
 */
public class IntrosDirectory extends SDirectory {
    private Object directory;
    private String fname;
    
    public IntrosDirectory(ADirectory dir, String fname) {
        this.directory = dir;
        this.fname = fname;
    }
    
    @Override
    public Map<String, IFile> listFiles() {
        Map<String, IFile> files = new HashMap<String, IFile>();
        
        for (Field aField : directory.getClass().getDeclaredFields()) {
            if (aField == null) {
                continue;
            }
            
            Object field_value;
            try {
                field_value = aField.get(directory) ;
            } catch (IllegalArgumentException ex) {
                //print(ex.getMessage());
                Logger.getLogger(net.wazari.libvfs.test.Test.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            } catch (IllegalAccessException ex) {
                Logger.getLogger(net.wazari.libvfs.test.Test.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            
            if (aField.isAnnotationPresent(File.class) && field_value instanceof IFile) {
                File fileAnnot = aField.getAnnotation(File.class);
                files.put(fileAnnot.name(), (IFile) field_value);
            }
            
            if (aField.isAnnotationPresent(Directory.class)) {
                Directory dirAnnot = aField.getAnnotation(Directory.class);
                files.put(dirAnnot.name(), new IntrosDirectory((ADirectory) field_value, dirAnnot.name()));
            } 
        }
        return files;
    }

    @Override
    public String getContent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getSize() {
        int r = 0;
        
        r |= 1 << 2; //read-only
        r = (r << 6) | (r << 3);        
        r |= Code.S_IFDIR;

        return r;
    }

    @Override
    public long getTime() {
        return 1;
    }

    @Override
    public String getShortname(IDirectory context) {
        if (fname == null) {
            return "/";
        }
        
        return fname;
    }

    @Override
    public boolean supports(long flags) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void incReference() {
    }

    @Override
    public void decReference() {
    }

    @Override
    public long getHandle() {
       return 100;
    }

    @Override
    public void release() {
        
    }

    @Override
    public void close() {
        
    }
    
}
