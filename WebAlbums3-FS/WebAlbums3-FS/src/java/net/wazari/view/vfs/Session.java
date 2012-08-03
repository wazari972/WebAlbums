/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;

/**
 *
 * @author kevin
 */
public class Session implements ViewSession {

    public Session(String string) {
        
    }

    
    
    @Override
    public boolean getCompleteChoix() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ViewSession.Special getSpecial() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ViewSession.Action getAction() {
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

