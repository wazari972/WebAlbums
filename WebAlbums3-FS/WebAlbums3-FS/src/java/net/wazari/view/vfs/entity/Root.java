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
import net.wazari.service.exchange.xml.XmlTheme;
import net.wazari.view.vfs.Launch;
import net.wazari.view.vfs.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@File
public class Root implements ADirectory {
    private static final Logger log = LoggerFactory.getLogger(Root.class.getCanonicalName()) ;
    
    @Directory
    public List<Theme> themes = new LinkedList<Theme>();
    private final Launch aThis;

    public Root(Launch aThis) throws WebAlbumsServiceException {
        this.aThis = aThis;
    }

    @Override
    public void load() throws Exception {
        log.warn("LOAD ROOT");
        for (XmlTheme theme : aThis.themeService.getThemeList(new Session(null)).theme) {
            log.warn("LOAD ROOT {}", theme.name);
            themes.add(new Theme(theme.id, theme.name, aThis));
        }
    }

    @Override
    public void unload() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}