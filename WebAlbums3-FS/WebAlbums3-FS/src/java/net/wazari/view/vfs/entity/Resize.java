/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.dao.entity.Theme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.CanChange;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.IDirectory;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Resize extends SDirectory implements ADirectory, CanChange {
    private static final Logger log = LoggerFactory.getLogger(Resize.class.toString());
    
    @File
    public List<Photo> photos = new LinkedList<Photo>();
    
    private final Theme theme;
    private final Launch aThis;

    public Resize(Theme theme, Launch aThis) {
        this.theme = theme;
        this.aThis = aThis;
    }
    
    private static Integer getId(String name) {
        try {
            return Integer.parseInt(name.split("\\.")[0]) ;
        } catch(NumberFormatException e) {
            return null;
        }
    }

    private static String getReducedPath(Integer id, String name, Theme theme, Launch aThis) throws WebAlbumsServiceException {
        if (id == null) {
            return null;
        }
        
        String path;
        Session session = new Session(theme);
        String[] names = name.split("\\.");
        
        session.setId(id);
        
        if (names.length > 1) {
            int width = Integer.parseInt(names[1]);
            session.setWidth(width);
            if (names.length > 2) {
                String color = names[2];
                session.setBorderColor(color);
                if (names.length > 3) {
                    Integer border = Integer.parseInt(names[3]);
                    session.setBorderWidth(border);
                }
            }
            path = aThis.imageService.treatSHRINK(session.getSessionImage());
        } else {
            path = aThis.photoService.treatABOUT(session.getSessionPhotoSimple()).details.photoId.path;
        }
        
        return path;
    }
    
    @Override
    public void create(String name) throws Exception {
        Integer id = getId(name);
        String path = getReducedPath(id, name, theme, aThis);
        
        Photo photo = new ResizePhoto(path, name, id, theme, aThis);
        photos.add(photo);
        changed = true;
    }
    
    @Override
    public void load() throws Exception {
    }

    private boolean changed = true;
    
    @Override
    public void contentRead() {
        changed = false;
    }

    @Override
    public boolean contentChanged() {
        return changed;
    }

    private static class ResizePhoto extends Photo {
        private Theme theme;
        private Launch aThis;

        public ResizePhoto(String path, String name, Integer id, Theme theme, Launch aThis) {
            super(path, name, id);
            this.theme = theme;
            this.aThis = aThis;
            
            this.doCompletePath = (name.indexOf(".") == -1);
        }
        
        @Override
        public void rename(IDirectory targetDir, String name) throws Exception {
            this.id = getId(name);
            log.warn("RENAME into {} ==> {}", name, id);
            if (this.id != null) {
                this.target = getReducedPath(id, name, theme, aThis);
            }
            this.name = name;
            this.forceFile = (this.id == null);
            this.doCompletePath = (name.indexOf(".") == -1);
        }
        
        @Override
        public void unlink() throws Exception {
            if (getParent() != null) {
                getParent().rmFile(this);
            }
        }
    }
}
