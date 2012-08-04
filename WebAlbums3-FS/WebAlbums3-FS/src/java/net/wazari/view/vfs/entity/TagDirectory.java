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
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.service.exchange.xml.tag.XmlTag;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;

/**
 *
 * @author kevin
 */
public class TagDirectory implements ADirectory {
    public enum WhatTag {WHO, WHERE, WHAT}
    
    @Directory
    @File
    public List<Tag> tags = new LinkedList<Tag>();
    
    private final Theme theme;
    private final Launch aThis;
    private final WhatTag what;
    
    public TagDirectory(net.wazari.dao.entity.Theme theme, Launch aThis, WhatTag what) throws WebAlbumsServiceException {
        this.theme = theme;
        this.aThis = aThis;
        this.what = what;
    }

    @Override
    public void load() throws Exception {
        Session session = new Session(theme);
        XmlWebAlbumsList entries = aThis.webPageService.displayListLB(ViewSession.Mode.TAG_USED, session, null,
                ViewSession.Box.MULTIPLE);
        
        List<XmlTag> tagList = new LinkedList<XmlTag>() ;
        if (what == WhatTag.WHO) {
            tagList.addAll(entries.who);
        }
        if (what == WhatTag.WHAT) {
            tagList.addAll(entries.what);
        }
        if (what == WhatTag.WHERE) {
            tagList.addAll(entries.where);
        }
        
        for (XmlTag tag : tagList) {
            tags.add(new Tag(tag.name, tag.id, theme, aThis)) ;
        }
    }

    @Override
    public void unload() {
    }
    
    @Override
    public String toString() {
        return "Directory["+theme.getNom()+"/tags]";
    }
}
