/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SLink;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.album.XmlAlbumSelect;
import net.wazari.view.vfs.Launch;

/**
 *
 * @author kevin
 */
public class Albums extends Listing implements ADirectory {

@Directory
    @File
    public List<Tag> albums = new LinkedList<Tag>();
    
    public Albums(ViewSession session, Launch aThis) throws WebAlbumsServiceException {
        XmlAlbumSelect entries = aThis.albumService.treatSELECT((ViewSessionAlbum) session);
        
        for (XmlAlbum album : entries.album) {
            albums.add(new Tag(album.albmDate+" "+album.name)) ;
        }
    }
}