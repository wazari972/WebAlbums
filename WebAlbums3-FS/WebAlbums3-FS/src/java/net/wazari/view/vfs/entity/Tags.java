/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import net.wazari.dao.entity.Theme;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.view.vfs.Launch;

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

    public Tags(Theme theme, Launch aThis, boolean b) throws WebAlbumsServiceException {
        who = new TagDirectory(theme, aThis, TagDirectory.WhatTag.WHO);
        
        what = new TagDirectory(theme, aThis, TagDirectory.WhatTag.WHAT);
        
        where = new TagDirectory(theme, aThis, TagDirectory.WhatTag.WHERE);
    }
    
    @Override
    public void load() throws Exception {
    }

    @Override
    public void unload() {
    }
}