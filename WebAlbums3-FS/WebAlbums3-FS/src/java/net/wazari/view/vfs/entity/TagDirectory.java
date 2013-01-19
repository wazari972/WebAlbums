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
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.service.exchange.xml.tag.XmlTag;
import net.wazari.service.exchange.xml.tag.XmlTagCloud.XmlTagCloudEntry;
import net.wazari.view.vfs.Launch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class TagDirectory extends SDirectory implements ADirectory {
    private static final Logger log = LoggerFactory.getLogger(TagDirectory.class.getCanonicalName()) ;
    
    @File
    @Directory
    public List<Tag> tagFiles = new LinkedList<Tag>();
    
    @File
    public GpxFile location;
    
    private final Theme theme;
    private final Launch aThis;
    private List<XmlTag> tagList = null;
    private List<XmlTag> tagCloud = null;
    protected final Integer tagId;
    
    public TagDirectory(XmlTag tag, net.wazari.dao.entity.Theme theme, Launch aThis) {
        this.theme = theme;
        this.aThis = aThis;
        
        if (tag != null) {
            this.tagId = tag.id;
        } else {
            this.tagId = null;
        }
        
        log.warn("===>"+tag);
        if (tag instanceof XmlWebAlbumsList.XmlWebAlbumsTagWhere) {
            XmlWebAlbumsList.XmlWebAlbumsTagWhere where = (XmlWebAlbumsList.XmlWebAlbumsTagWhere) tag;
            log.warn("===> try new LOCATION "+where.lat +"/" +where.longit);
            if (where.lat != null && where.longit != null) {
                GpxPoints loc = new GpxPoints(tag.name, where.lat, where.longit);
                location = new GpxFile(loc);
                log.warn("===> NEW LOCATION "+where.lat +"/" +where.longit);
            }
        }
        
        if (tag != null && tag.loc != null) {
            log.warn("===> try new LOCATION 2"+tag.loc.lat +"/" +tag.loc.longit);
            if (tag.loc.lat != null && tag.loc.longit != null) {
                GpxPoints loc = new GpxPoints(tag.name, tag.loc.lat, tag.loc.longit);
                location = new GpxFile(loc);
                log.warn("===> NEW LOCATION 2"+tag.loc.lat +"/" +tag.loc.longit);
            }
        }
    }
    
    public TagDirectory(XmlTag tag, net.wazari.dao.entity.Theme theme, Launch aThis, List<XmlTag> tagList) {
        this(tag, theme, aThis);
        this.tagList = tagList;
        
    }

    public TagDirectory(XmlTag tag, List<XmlTag> tagCloud, Theme theme, Launch aThis) {
        this(tag, theme, aThis);
        this.tagCloud = tagCloud;
    }

    @Override
    public void load() throws Exception {
        log.warn("Load directories from : {}", this);
        if (tagList != null) {
            for (XmlTag tag : tagList) {
                tagFiles.add(new Tag(tag, theme, aThis)) ;
            }
        } else if (tagCloud != null) {
            for (XmlTag tag : tagCloud) {
                tag.name = ((XmlTagCloudEntry) tag).nb + " " + tag.name;
                tagFiles.add(new Tag(tag.children, tag, theme, aThis)) ;
            }
        }
    }
    
    @Override
    public String toString() {
        return "Directory["+theme.getNom()+"/tags]";
    }
}
