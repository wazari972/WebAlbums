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
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.album.XmlAlbumSelect;
import net.wazari.view.vfs.FSConnector;
import net.wazari.view.vfs.Session;

/**
 *
 * @author kevin
 */
public class Albums extends Listing implements ADirectory {
    @Directory
    @File
    public List<BasicDirectory> years = new LinkedList<>();
    
    private final Theme theme;
    private final FSConnector aThis;
    private final Root root;
    
    public Albums(Root root, net.wazari.dao.entity.Theme theme, FSConnector aThis) {
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
        
        Map<String, BasicDirectory> yearalbums = new HashMap<>();
        for (XmlAlbum album : entries.album) {
            String key = album.date.year;
            BasicDirectory year;
            if (!yearalbums.containsKey(key)) {
                year = new BasicDirectory(key);
                yearalbums.put(key, year);
                years.add(year);
            } else {
                year = yearalbums.get(key);
            }
            
            year.addDirInside(new Album(this.root, album.date, album.name, album.id, theme, aThis)) ;
        }
    }    
}