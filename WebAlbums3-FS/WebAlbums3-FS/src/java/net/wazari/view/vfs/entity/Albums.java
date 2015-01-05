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
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exception.WebAlbumsServiceException;
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
    public List<Album> albums = new LinkedList<>();
    private final Theme theme;
    private final Launch aThis;
    private final Root root;
    public Albums(Root root, net.wazari.dao.entity.Theme theme, Launch aThis) {
        this.theme = theme;
        this.aThis = aThis;
        this.root = root;
    }

    @Override
    public void load() throws VFSException {
        Session session = new Session(theme, root);
        XmlAlbumSelect entries;
        try {
            entries = aThis.albumService.treatSELECT(session.getSessionAlbumSelect());
        } catch (WebAlbumsServiceException ex) {
            throw new VFSException(ex);
        }
        
        for (XmlAlbum album : entries.album) {
            albums.add(new Album(this.root, album.date, album.name, album.id, theme, aThis)) ;
        }
    }
}