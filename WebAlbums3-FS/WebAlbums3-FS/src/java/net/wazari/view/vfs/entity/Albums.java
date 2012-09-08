/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.dao.entity.Theme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.album.XmlAlbumSelect;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;

/**
 *
 * @author kevin
 */
public class Albums extends Listing implements ADirectory {
    @Directory
    @File
    public List<Album> albums = new LinkedList<Album>();
    private final Theme theme;
    private final Launch aThis;
    
    public Albums(net.wazari.dao.entity.Theme theme, Launch aThis) throws WebAlbumsServiceException {
        this.theme = theme;
        this.aThis = aThis;
    }

    @Override
    public void load() throws Exception {
        Session session = new Session(theme);
        XmlAlbumSelect entries = aThis.albumService.treatSELECT((ViewSessionAlbum) session);
        
        for (XmlAlbum album : entries.album) {
            albums.add(new Album(album.albmDate, album.name, album.id, theme, aThis)) ;
        }
    }
}