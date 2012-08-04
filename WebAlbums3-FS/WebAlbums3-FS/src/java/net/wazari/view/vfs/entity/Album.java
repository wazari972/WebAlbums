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
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.xml.photo.XmlPhoto;
import net.wazari.service.exchange.xml.photo.XmlPhotoDisplay;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;

/**
 *
 * @author kevin
 */
public class Album extends SDirectory implements ADirectory {
    
    @File
    public List<Photo> photos = new LinkedList<Photo>();
    
    private String name ;
    private final Theme theme;
    private final Launch aThis;
    private final int albumId;
    
    public Album(String date, String name, int albumId, net.wazari.dao.entity.Theme theme, Launch aThis) {
        this.name = date + " " + name;
        this.theme = theme;
        this.aThis = aThis;
        this.albumId = albumId;
    }
    
    @Override
    public String getShortname() {
        return name;
    }

    @Override
    public void load() throws Exception {
        Session session = new Session(theme);
        session.setAlbum(albumId);
        
        XmlPhotoDisplay photodisp = aThis.photoService.treatPhotoDISPLAY((ViewSessionPhoto.ViewSessionPhotoDisplay) session, null);
        for (XmlPhoto photo : photodisp.photoList.photo) {
            photos.add(new Photo(photo.details));
        }
    }

    @Override
    public void unload() {
    }
    
    @Override
    public String toString() {
        return "Directory[tags/"+name+"]";
    }
    
}
