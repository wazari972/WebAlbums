/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import com.jnetfs.core.relay.impl.JnetFSAdapter;
import com.jnetfs.vfs.dbfs.JnetDBFS;
import java.util.LinkedList;
import java.util.List;
import net.wazari.dao.entity.Theme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.libvfs.inteface.SDirectory;
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
    
    @Directory
    @File
    public List<Tag> tagFiles = new LinkedList<Tag>();
    
    private final Theme theme;
    private final Launch aThis;
    private List<XmlTag> tagList = null;
    private List<XmlTagCloudEntry> tagCloud = null;
    
    public TagDirectory(net.wazari.dao.entity.Theme theme, Launch aThis, List<XmlTag> tagList) {
        this.theme = theme;
        this.aThis = aThis;
        this.tagList = tagList;
    }

    public TagDirectory(List<XmlTagCloudEntry> tagCloud, Theme theme, Launch aThis) {
        this.theme = theme;
        this.aThis = aThis;
        this.tagCloud = tagCloud;
    }

    @Override
    public void load() throws Exception {
        log.warn("Load directories from : {}", this);
        if (tagList != null) {
            for (XmlTag tag : tagList) {
                tagFiles.add(new Tag(tag.name, tag.id, theme, aThis)) ;
            }
        } else if (tagCloud != null) {
            for (XmlTagCloudEntry tag : tagCloud) {
                log.warn("Add directory: {} ", tag.name);
                tagFiles.add(new Tag(tag.tag, tag.nb+" "+tag.name, tag.id, theme, aThis)) ;
            }
        }
    }
    
    @Override
    public String toString() {
        return "Directory["+theme.getNom()+"/tags]";
    }
}
