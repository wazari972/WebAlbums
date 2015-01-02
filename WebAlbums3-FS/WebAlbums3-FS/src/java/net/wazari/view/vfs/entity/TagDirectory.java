/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.dao.entity.Theme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.CanChange;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.IFile;
import net.wazari.libvfs.inteface.SDirectory;
import net.wazari.libvfs.inteface.VFSException;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.service.exchange.xml.tag.XmlTag;
import net.wazari.service.exchange.xml.tag.XmlTagCloud.XmlTagCloudEntry;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class TagDirectory extends SDirectory implements ADirectory, CanChange {
    private static final Logger log = LoggerFactory.getLogger(TagDirectory.class.getCanonicalName()) ;
    
    @File
    @Directory
    public List<Tag> tagFiles;
    
    @File
    public GpxFile location;
    
    private final Theme theme;
    private final Launch aThis;
    private List<XmlTag> tagList = null;
    private List<XmlTagCloudEntry> tagCloud = null;
    protected final Integer tagId;
    
    private boolean contentChanged = true;
    
    public TagDirectory(XmlTag tag, Theme theme, Launch aThis) {
        this.theme = theme;
        this.aThis = aThis;
        
        if (tag != null) {
            this.tagId = tag.id;
        } else {
            this.tagId = null;
        }
        
        if (tag instanceof XmlWebAlbumsList.XmlWebAlbumsTagWhere) {
            XmlWebAlbumsList.XmlWebAlbumsTagWhere where = (XmlWebAlbumsList.XmlWebAlbumsTagWhere) tag;
            if (where.lat != null && where.lng != null) {
                GpxPoints loc = new GpxPoints(tag.name, where.lat, where.lng);
                location = new GpxFile(loc);
            }
        }
    }
    
    public TagDirectory(XmlTag tag, Theme theme, Launch aThis, List<XmlTag> tagList) {
        this(tag, theme, aThis);
        this.tagList = tagList;
        
    }

    public TagDirectory(XmlTag tag, List<XmlTagCloudEntry> tagCloud, Theme theme, Launch aThis) {
        this(tag, theme, aThis);
        this.tagCloud = tagCloud;
    }

    @Override
    public void load() throws VFSException {
        log.warn("Load directories from : {} ? {}", this, contentChanged);
        if (!contentChanged) {
            return;
        }
        
        tagFiles = new LinkedList<Tag>(); // empty first, because CanChange
        contentRead();
        
        if (tagList != null) {
            for (XmlTag tag : tagList) {
                tagFiles.add(new Tag(tag, theme, aThis)) ;
            }
        } else if (tagCloud != null) {
            for (XmlTagCloudEntry tag : tagCloud) {
                tag.tag.name = tag.nb + " " + tag.tag.name;
                tagFiles.add(new Tag(tag.children, tag.tag, theme, aThis)) ;
            }
        }
    }
    
    @Override
    public String toString() {
        return "Directory["+theme.getNom()+"/tags]";
    }
    
    @Override
    public void acceptNewFile(IFile srcFile, String filename) throws VFSException {
        if (!(srcFile instanceof Photo)) {
            log.warn("TagDirectory cannot accept {}", srcFile);
        }
        
        Photo srcPhoto = (Photo) srcFile;
        
        Session session = new Session(theme);
        session.setId(srcPhoto.id);
        session.tagAction = ViewSessionPhoto.ViewSessionPhotoFastEdit.TagAction.ADD;
        session.tagSet = new Integer[]{this.tagId};
        try {
            this.aThis.photoService.treatFASTEDIT(session.getSessionPhotoFastEdit());
            contentChanged = true;
        } catch (WebAlbumsServiceException ex) {
            throw new VFSException(ex.getMessage());
        }
    }

    
    @Override
    public void moveIn(IFile srcFile, String filename) throws VFSException {
        acceptNewFile(srcFile, filename);
    }

    @Override
    public void contentRead() {
        contentChanged = false;
    }

    @Override
    public boolean contentChanged() {
        return contentChanged;
    }
}
