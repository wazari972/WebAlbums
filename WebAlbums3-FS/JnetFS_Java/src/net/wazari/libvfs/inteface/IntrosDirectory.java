/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.libvfs.inteface;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.CanChange;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class IntrosDirectory extends SDirectory {
    private static final Logger log = LoggerFactory.getLogger(IntrosDirectory.class.getCanonicalName());
    
    private List<IFile> inFiles = null;
    private final ADirectory directory;
    
    public IntrosDirectory(IDirectory parent, ADirectory adir) {
        this.parent = parent;
        this.directory = adir;
        if (adir instanceof IFile) {
            ((IFile) adir).setParent(parent);
        }
    }
    
    public ADirectory getADirectory() {
        return this.directory;
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
            log.warn("getAccess no field/no annot {}", theField);
            return null;
        }
        
        File fileAnnot = theField.getAnnotation(File.class);
        if (fileAnnot.access().length != 0) {
            return fileAnnot.access();
        } else {
            log.warn("getAccess empty");
            return null;
        }
    }
        
    private Map<Object, Field> getDirFields() {
        Map<Object, Field> map = new HashMap<>();
        Class clazz = directory.getClass();
        while (clazz != null) {
            for (Field aField : clazz.getDeclaredFields()) {
                if (aField == null) {
                    continue;
                }

                Object field_value;
                try {
                    field_value = aField.get(directory) ;
                } catch (IllegalArgumentException | IllegalAccessException ex) {
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
            clazz = clazz.getSuperclass();
        }
        return map;
    }
    
    @Override 
    public void addFile(IFile file) {
        if (inFiles == null) {
            return;
        }
        
        inFiles.add(file);
    }
    
    @Override 
    public void rmFile(IFile file) {
        if (inFiles == null) {
            return;
        }
        
        inFiles.remove(file);
    }
    
    @Override
    public List<IFile> listFiles() {
        boolean changed = directory instanceof CanChange && ((CanChange) directory).contentChanged();
        
        //if it's not the first time we come here the dir can change and did not change
        if (inFiles != null && !changed) {
            return inFiles;
        }
        try {
            directory.load();
            log.debug("Loaded {}", this.directory);
        } catch (Exception ex) {
            log.warn("Loading {} failed ... ", this.directory, ex);
        }
        
        inFiles = new LinkedList<>();

        Map<Object, Field> map = getDirFields();
        for (Object field_value : map.keySet()) {
            Field aField = map.get(field_value);
            if (aField.isAnnotationPresent(File.class) && field_value instanceof IFile && !(field_value instanceof ADirectory)) {
                IFile son = (IFile) field_value;
                son.setParent(this);
                inFiles.add(son);
            } else if (aField.isAnnotationPresent(Directory.class)) {
                if (field_value instanceof ADirectory) {
                    ADirectory adir = (ADirectory) field_value;
                    inFiles.add(new IntrosDirectory(this, adir));
                } else if (field_value instanceof List) {
                    for (Object afile : (List) field_value) {
                        if (afile instanceof ADirectory) {
                            inFiles.add(new IntrosDirectory(this, (ADirectory) afile));
                        } else if (afile instanceof IFile) {
                            inFiles.add((IFile) afile);
                        }
                    }
                }
            }
        }
        
        if (directory instanceof CanChange) {
            ((CanChange) directory).contentRead();
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
        }
    }
    
    @Override
    public void create(String path) throws Exception {
        if (directory instanceof IDirectory) {
            ((IDirectory) directory).create(path);
        }
    }

    @Override
    public void moveIn(IFile srcFile, String filename) throws VFSException {
        if (directory instanceof IDirectory) {
            ((IDirectory) directory).moveIn(srcFile, filename);
        }
    }
    
    @Override
    public void acceptNewFile(IFile file, String filename) throws VFSException {
        if (directory instanceof IDirectory) {
            ((IDirectory) directory).moveIn(file, filename);
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

