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

/**
 *
 * @author kevin
 */
@File
public class Root implements ADirectory {
    @Directory
    public List<Theme> themes = new LinkedList<Theme>();

    public Root(Launch aThis) throws WebAlbumsServiceException {
        for (XmlTheme theme : aThis.themeService.getThemeList(new Session(null)).theme) {
            themes.add(new Theme(theme.id, theme.name, aThis));
        }
    }
}