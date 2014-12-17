/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.dao.entity.Theme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.xml.common.XmlDate;
import net.wazari.service.exchange.xml.photo.XmlPhoto;
import net.wazari.service.exchange.xml.photo.XmlPhotoDisplay;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Album extends SDirectory implements ADirectory {
    private static final Logger log = LoggerFactory.getLogger(Album.class.getCanonicalName()) ;
    
    @File
    public List<Photo> photos = new LinkedList<>();
    
    private final String name ;
    private final Theme theme;
    private final Launch aThis;
    private final int albumId;
    
    public Album(XmlDate date, String name, int albumId, net.wazari.dao.entity.Theme theme, Launch aThis) {
        this.name = date.date + " " + name;
        this.theme = theme;
        this.aThis = aThis;
        this.albumId = albumId;
    }
    
    @Override
    public String getShortname() {
        return name;
    }

    @Override
    public void load() throws VFSException {
        Session session = new Session(theme);
        session.setAlbum(albumId);
        
        XmlPhotoDisplay photodisp;
        try {
            photodisp = aThis.photoService.treatPhotoDISPLAY(session.getSessionPhotoDisplay(), null);
        } catch (WebAlbumsServiceException ex) {
            throw new VFSException(ex);
        }
        for (XmlPhoto photo : photodisp.photoList.photo) {
            photos.add(new Photo(photo.details));
        }
    }
    
    @Override
    public String toString() {
        return "Directory[Album/"+name+"]";
    }
    
    @Override
    public void moveIn(IFile srcFile, String filename) {
        log.warn("hello {} / a{}", filename, albumId);
    }
}
