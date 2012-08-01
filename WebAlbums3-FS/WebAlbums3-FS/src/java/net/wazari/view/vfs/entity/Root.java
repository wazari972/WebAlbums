/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.util.LinkedList;
import java.util.List;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.libvfs.annotation.ADirectory;
import net.wazari.libvfs.annotation.Directory;
import net.wazari.libvfs.annotation.File;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.XmlTheme;
import net.wazari.view.vfs.Launch;

/**
 *
 * @author kevin
 */
@File
public class Root implements ADirectory {
    @Directory
    public List<Theme> themes = new LinkedList<Theme>();

    public Root(Launch aThis) {
        for (XmlTheme theme : aThis.themeService.getThemeList(new Session()).theme) {
            themes.add(new Theme(theme.name));
        }
    }
}

class Session implements ViewSession {

    @Override
    public boolean getCompleteChoix() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Special getSpecial() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Action getAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Utilisateur getUser() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAuthenticated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public java.io.File getTempDir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Configuration getConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getThemeId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRemoteAccess() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean directFileAccess() {
        return false;
    }

    @Override
    public void setDirectFileAccess(boolean access) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setStatic(boolean statik) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getStatic() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public net.wazari.dao.entity.Theme getTheme() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAdminSession() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRootSession() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSessionManager() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getPhotoAlbumSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
