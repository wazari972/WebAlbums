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
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.service.exchange.xml.tag.XmlTagCloud;
import net.wazari.view.vfs.Launch;
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
    private final Launch aThis;

    public Tags(Theme theme, Launch aThis) {
        this.theme = theme;
        this.aThis = aThis;
    }
    
    @Override
    public void load() throws Exception {
        Session session = new Session(theme);
        XmlWebAlbumsList entries = aThis.webPageService.displayListLB(ViewSession.Mode.TAG_USED, session, null,
                ViewSession.Box.MULTIPLE);
        
        who = new TagDirectory(theme, aThis, (List) entries.who);
        what = new TagDirectory(theme, aThis, (List) entries.what);
        where = new TagDirectory(theme, aThis, (List) entries.where);
        
        XmlTagCloud theCloud = aThis.tagService.treatTagCloud(session);
        cloud = new TagDirectory(theCloud.parentList, theme, aThis);
    }
}