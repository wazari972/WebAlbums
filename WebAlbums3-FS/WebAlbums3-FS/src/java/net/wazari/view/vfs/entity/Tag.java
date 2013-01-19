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
import net.wazari.libvfs.inteface.IDirectory;
import net.wazari.libvfs.inteface.IntrosDirectory;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit.TagAction;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.service.exchange.xml.photo.XmlPhoto;
import net.wazari.service.exchange.xml.tag.XmlTag;
import net.wazari.service.exchange.xml.tag.XmlTagDisplay;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class Tag extends TagDirectory {
    private static final Logger log = LoggerFactory.getLogger(Tag.class.getCanonicalName()) ;
    
    @File
    public List<TagPhoto> photos = new LinkedList<TagPhoto>();
    
    private String name ;
    private final Theme theme;
    private final Launch aThis;
    
    public Tag(XmlTag tag, Theme theme, Launch aThis) {
        this(null, tag, theme, aThis);
    }
    
    public Tag(List<XmlTag> tagInside, XmlTag tag, Theme theme, Launch aThis) {
        super(tag, tagInside, theme, aThis);
        this.name = tag.name;
        this.theme = theme;
        this.aThis = aThis;
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
        log.warn("Load images from : {} == {} images", this, tags.photoList.photo.size());
        for (XmlPhoto photo : tags.photoList.photo) {
            photos.add(new TagPhoto(theme, aThis, photo.details, true));
        }
        super.load();
    }
    
    @Override
    public String toString() {
        return "Directory[tags/"+name+"]";
    }

    public static class TagPhoto extends Photo {

        private final Theme theme;
        private final Launch aThis;
        
        public TagPhoto(Theme theme, Launch aThis, XmlDetails details, boolean uniqName) {
            super(details, uniqName);
            this.theme = theme;
            this.aThis = aThis;
        }
        
        @Override
        public void unlink() throws Exception {
            Session session = new Session(theme);
            
            if (!(getParent() instanceof IntrosDirectory)) {
                throw new Exception("Dir "+getParent().getShortname() + "<>" + getParent() + " is not a IntrosDirectory");
            }
            
            ADirectory adir = ((IntrosDirectory) getParent()).getADirectory();
            if (!(adir instanceof TagDirectory)) {
                throw new Exception("Dir "+ adir + " is not a TagDirectory");
            }
            
            TagDirectory aTagDir = (TagDirectory) adir;
            
            session.setId(this.id);
            session.setTagAction(TagAction.RM);
            session.setTagSet(new Integer[]{aTagDir.tagId});
            this.aThis.photoService.treatFASTEDIT(session);
            
             ((IntrosDirectory) getParent()).rmFile(this);
            
            log.warn("UNLIK: Tag {} removed from Photo {}", aTagDir.getShortname(), this.getShortname());
        }
        
        @Override
        public void rename(IDirectory targetDir, String filename) throws Exception {
            log.warn("RENAME to {} in {}", targetDir, filename);
            
            if (!(targetDir instanceof IntrosDirectory)) {
                throw new Exception("Dir "+targetDir.getShortname() + "<>" + targetDir + " is not a IntrosDirectory");
            }
            
            ADirectory adir = ((IntrosDirectory) targetDir).getADirectory();
            if (!(adir instanceof TagDirectory)) {
                throw new Exception("Dir "+ adir + " is not a TagDirectory");
            }
            
            TagDirectory aTagDir = (TagDirectory) adir;
            
            Session session = new Session(theme);
            
            session.setId(this.id);
            session.setTagAction(TagAction.ADD);
            session.setTagSet(new Integer[]{aTagDir.tagId});
            this.aThis.photoService.treatFASTEDIT(session);
            
            ((IntrosDirectory) targetDir).addFile(this);
            
            log.warn("RENAME: Tag {} added to Photo {}", getParent().getShortname(), this.getShortname());
            
        }
    }
}
