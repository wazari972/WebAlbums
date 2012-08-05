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
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exchange.xml.photo.XmlPhoto;
import net.wazari.service.exchange.xml.tag.XmlTagCloud;
import net.wazari.service.exchange.xml.tag.XmlTagDisplay;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Tag extends TagDirectory implements ADirectory {
    private static final Logger log = LoggerFactory.getLogger(Tag.class.getCanonicalName()) ;
    
    @File
    public List<Photo> photos = new LinkedList<Photo>();
    
    private String name ;
    private final Theme theme;
    private final Launch aThis;
    private final int tagId;
    
    public Tag(String name, int tagId, net.wazari.dao.entity.Theme theme, Launch aThis) {
        this(null, name, tagId, theme, aThis);
    }
    
    
    public Tag(List<XmlTagCloud.XmlTagCloudEntry> tagInside, String name, int tagId, net.wazari.dao.entity.Theme theme, Launch aThis) {
        super(tagInside, theme, aThis);
        this.name = name;
        this.theme = theme;
        this.aThis = aThis;
        this.tagId = tagId;
    }
    
    @Override
    public String getShortname() {
        return name;
    }

    @Override
    public void load() throws Exception {
        log.warn("Load images from : {}", this);
        Session session = new Session(theme);
        session.setTagAsked(new Integer[]{tagId});
        XmlTagDisplay tags = aThis.tagService.treatTagDISPLAY((ViewSessionTag) session, null);
        for (XmlPhoto photo : tags.photoList.photo) {
            photos.add(new Photo(photo.details));
        }
        super.load();
    }

    @Override
    public void unload() {
    }
    
    @Override
    public String toString() {
        return "Directory[tags/"+name+"]";
    }
    
}
