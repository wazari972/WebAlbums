/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.List;
import net.wazari.dao.entity.Theme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.service.exchange.xml.tag.XmlTagCloud;
import net.wazari.view.vfs.FSConnector;
import net.wazari.view.vfs.Session;

/**
 *
 * @author kevin
 */
public class Tags implements ADirectory {
    @Directory
    @File(name="Geolocalisations")
    public TagDirectory where ;

    @Directory
    @File(name="Personnes")
    public TagDirectory who ;
    
    @Directory
    @File(name="QuoiCa")
    public TagDirectory what ;
    
    @Directory
    @File(name="Cloudy")
    public TagDirectory cloud ;
    
    private final Theme theme;
    private final FSConnector aThis;
    private final Root root;
    
    public Tags(Root root, Theme theme, FSConnector aThis) {
        this.theme = theme;
        this.aThis = aThis;
        this.root = root;
    }
    
    @Override
    public void load() throws VFSException {
        Session session = new Session(theme, this.root);
        
        XmlWebAlbumsList entries;
        try {
            entries = aThis.webPageService.displayListLB(ViewSession.Tag_Mode.TAG_USED, session, null, ViewSession.Box.MULTIPLE);
        } catch (WebAlbumsServiceException ex) {
            throw new VFSException(ex);
        }
        
        who = new TagDirectory(root, null, theme, aThis, (List) entries.who);
        what = new TagDirectory(root, null, theme, aThis, (List) entries.what);
        where = new TagDirectory(root, null, theme, aThis, (List) entries.where);
        
        XmlTagCloud theCloud = aThis.tagService.treatTagCloud(session);
        cloud = new TagDirectory(root, null, theCloud.parentList, theme, aThis);
    }
}