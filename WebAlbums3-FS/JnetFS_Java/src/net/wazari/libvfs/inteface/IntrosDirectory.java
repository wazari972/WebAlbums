/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import com.jnetfs.core.relay.impl.JnetFSAdapter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class IntrosDirectory extends SDirectory {
    private static final Logger log = LoggerFactory.getLogger(IntrosDirectory.class.getCanonicalName()) ;
    
    private List<IFile> inFiles = null;
    private ADirectory directory;
    
    public IntrosDirectory(IntrosDirectory parent, ADirectory adir) {
        this.parent = parent;
        this.directory = adir;
    }
    
    @Override
    public File.Access[] getAccess() {
        File.Access[] access = null;
        if (getParent() != null) {
            access = getParent().getAccess(this);
        }
        
        if (access != null) {
            return access;
        }
        
        return new File.Access[]{File.Access.R};
    }
    
    @Override
    public File.Access[] getAccess(IFile file) {
        Object target = file;
        
        if (file instanceof IntrosDirectory) {
            target = ((IntrosDirectory) file).directory;
        }
        
        Field theField = null;
        Map<Object, Field> map = getDirFields();
        for (Object field_value : map.keySet()) {
            Field aField = map.get(field_value);
            
            if (field_value == target) {
                theField = aField;
                break;
            }
        }
        
        if (theField == null || !(theField.isAnnotationPresent(File.class))) {
            JnetFSAdapter.debug("getAccess no field/no annot "+theField);
            return null;
        }
        
        File fileAnnot = theField.getAnnotation(File.class);
        if (fileAnnot.access().length != 0) {
            return fileAnnot.access();
        } else {
            JnetFSAdapter.debug("getAccess empty");
            return null;
        }
    }
        
    private Map<Object, Field> getDirFields() {
        Map<Object, Field> map = new HashMap<Object, Field>();
        for (Field aField : directory.getClass().getDeclaredFields()) {
            if (aField == null) {
                continue;
            }
            
            Object field_value;
            try {
                field_value = aField.get(directory) ;
            } catch (IllegalArgumentException ex) {
                //print(ex.getMessage());
                //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                continue;
            } catch (IllegalAccessException ex) {
                //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            if (field_value instanceof List) {
                
                for (Object o : (List) field_value) {
                    map.put(o, aField);
                }
            } else {
                map.put(field_value, aField);
            }
        }
        return map;
    }
    
    private Map<ADirectory, IntrosDirectory> cache = new HashMap<ADirectory, IntrosDirectory>();
    @Override
    public List<IFile> listFiles() {
        if (inFiles != null) {
            return inFiles;
        }
        
        inFiles = new LinkedList<IFile>();
        try {
            log.warn("Load {}", this.directory);
            directory.load();
            log.warn("Loaded {}", this.directory);
        } catch (Exception ex) {
            log.warn("Loading {} failed ... {}", this.directory, ex);
        }
        
        Map<Object, Field> map = getDirFields();
        for (Object field_value : map.keySet()) {
            Field aField = map.get(field_value);
            
            if (aField.isAnnotationPresent(File.class) && field_value instanceof IFile && !(field_value instanceof ADirectory)) {
                IFile son = (IFile) field_value;
                son.setParent(this);
                inFiles.add(son);
            } else if (aField.isAnnotationPresent(Directory.class)) {
                IntrosDirectory toAdd = null;
                
                if (field_value instanceof ADirectory) {
                    inFiles.add(new IntrosDirectory(this, (ADirectory) field_value));
                } else if (field_value instanceof List) {
                    for (ADirectory adir : (List<ADirectory>) field_value) {
                        inFiles.add(new IntrosDirectory(this, (ADirectory) adir));
                    }
                }
            }
        }
        return inFiles;
    }
    
    @Override
    public String getShortname(IFile file) {
        Object target = file;
        
        if (file instanceof IntrosDirectory) {
            target = ((IntrosDirectory) file).directory;
        }
        
        Map<Object, Field> map = getDirFields();
        Field theField = null;
        for (Object field_value : map.keySet()) {
            Field aField = map.get(field_value);
            if (field_value == target) {
                theField = aField;
                break;
            } 
        }
        if (theField == null) {
            return null;
        }
        File fileAnnot = theField.getAnnotation(File.class) ;
                
        if (fileAnnot != null && fileAnnot.name().length() != 0) {
            return fileAnnot.name();
        } else if (target instanceof IDirectory) {
            return ((IDirectory) target).getShortname();
        } else {
            return null;
        }
    }
    
    @Override
    public void rmdir() {
        if (directory instanceof IDirectory) {
            ((IDirectory) directory).rmdir();
            inFiles = null;
        }
    }
    
    public static class IntrosRoot extends IntrosDirectory {
        private final Class clazz;
        public IntrosRoot(ADirectory adir) {
            super(null, adir);
            this.clazz = adir.getClass();
        }

        @Override
        public File.Access[] getAccess() {
            return ((File) clazz.getAnnotation(File.class)).access();
        }

        @Override
        public String getShortname() {
            return ((File) clazz.getAnnotation(File.class)).name();
        }
    }
}

