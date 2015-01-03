/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.wazari.dao.entity.Theme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.BasicDirectory;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumAgo;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.album.XmlAlbumAgo;
import net.wazari.service.exchange.xml.common.XmlDate;
import net.wazari.service.exchange.xml.photo.XmlPhoto;
import net.wazari.service.exchange.xml.photo.XmlPhotoDisplay;
import net.wazari.service.exchange.xml.tag.XmlTag;
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
    
    @Directory
    @File
    public List<SDirectory> subdirs = new LinkedList<>();
    
    public Map<Integer, SDirectory> phototags = new HashMap<>();
    
    private final String name ;
    private final Theme theme;
    private final Launch aThis;
    private final int albumId;
    
    /* set to TRUE to exclude subdirectories (tags and years ago). */
    private boolean simpleAlbumDir = false;
    
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
        final Session session = new Session(theme);
        session.setAlbum(albumId);
        
        XmlPhotoDisplay photodisp;
        try {
            photodisp = aThis.photoService.treatPhotoDISPLAY(session.getSessionPhotoDisplay(), null);
        } catch (WebAlbumsServiceException ex) {
            throw new VFSException(ex);
        }
        
        for (XmlPhoto photo : photodisp.photoList.photo) {
            Photo photofile = new Photo(photo.details);
            photos.add(photofile);
            if (simpleAlbumDir) {
                continue;
            }
            for (XmlTag tag : photo.details.tag_used.getAllTags()) {
                BasicDirectory tagdir;
                if (!phototags.containsKey(tag.id)) {
                    tagdir = new BasicDirectory(tag.name);
                    phototags.put(tag.id, tagdir);
                    subdirs.add(tagdir);
                } else {
                    tagdir = (BasicDirectory) phototags.get(tag.id);
                }
                tagdir.addFileInside(photofile);
            }
        }
        if (simpleAlbumDir) {
                return;
        }
        XmlAlbumAgo albumAgo;
        try {
            final XmlAlbum album = photodisp.album;
            ViewSessionAlbumAgo asession = new ViewSessionAlbum.ViewSessionAlbumAgo() {

                @Override
                public Integer getYear() {
                    return null;
                }

                @Override
                public Integer getMonth() {
                    return Integer.parseInt(album.date.date.split("-")[1]);
                }

                @Override
                public Integer getDay() {
                    return Integer.parseInt(album.date.day);
                }

                @Override
                public boolean getAll() {
                    return true;
                }

                @Override
                public Integer getExceptAlbm() {
                    return album.id;
                }

                @Override
                public ViewSession getVSession() {
                    return session;
                }
            };
            albumAgo = aThis.albumService.treatAGO(asession);
        } catch (WebAlbumsServiceException ex) {
            throw new VFSException(ex);
        }
        log.debug("Album {} has {} years-ago albums.", albumId, albumAgo.album.size());
        BasicDirectory ago = new BasicDirectory("Years before");
        for (XmlAlbum album : albumAgo.album) {
            Album albumagodir = new Album(album.date, album.name, album.id, theme, aThis);
            albumagodir.simpleAlbumDir = true;
            ago.addDirInside(albumagodir);
        }
        if (!ago.dirs.isEmpty()) {
            subdirs.add(ago);
        }
    }
    
    @Override
    public String toString() {
        return "Directory[Album/"+name+"]";
    }
    
    @Override
    public void moveIn(IFile srcFile, String filename) {
        log.warn("Album#{}::moveIn: {}", albumId, filename);
    }
}
