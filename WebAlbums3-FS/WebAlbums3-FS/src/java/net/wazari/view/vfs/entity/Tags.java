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
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.service.exchange.xml.tag.XmlTag;
import net.wazari.view.vfs.Launch;

/**
 *
 * @author kevin
 */
public class Tags implements ADirectory {
    @Directory
    @File
    public List<Tag> tags = new LinkedList<Tag>();
    
    public Tags(ViewSession session, Launch aThis, boolean geoOnly) throws WebAlbumsServiceException {
        XmlWebAlbumsList entries = aThis.webPageService.displayListLB(ViewSession.Mode.TAG_USED, session, null,
                ViewSession.Box.MULTIPLE);
        
        List<XmlTag> tagList = new LinkedList<XmlTag>() ;
        if (!geoOnly) {
            tagList.addAll(entries.who);
            tagList.addAll(entries.what);
        }
        tagList.addAll(entries.where);
        
        for (XmlTag tag : tagList) {
            tags.add(new Tag(tag.name)) ;
        }
    }
}
