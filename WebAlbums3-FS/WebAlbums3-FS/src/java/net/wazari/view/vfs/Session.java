/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import java.util.List;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionTag;

/**
 *
 * @author kevin
 */
public class Session implements ViewSession, ViewSessionTag, ViewSessionAlbum {
    public Theme theme;
    public Session(Theme theme) {
        this.theme = theme;
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
        return new Utilisateur() {

            @Override
            public List<Album> getAlbumList() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public String getNom() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setAlbumList(List<Album> albumList) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setId(Integer id) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void setNom(String nom) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Integer getId() {
                return 1;
            }
        };
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
        return theme;
    }

    @Override
    public boolean isAdminSession() {
        return false;
    }

    @Override
    public boolean isRootSession() {
        return theme.getId() == 0;
    }

    @Override
    public boolean isSessionManager() {
        return true;
    }

    @Override
    public int getPhotoAlbumSize() {
        return 15;
    }    

    @Override
    public Integer[] getTagAsked() {
        return new Integer[0];
    }

    @Override
    public boolean getWantTagChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getTagPhoto() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mode getMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getAlbum() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getAlbmPage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getPage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getNbPerYear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPhotoAlbumSize(int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

