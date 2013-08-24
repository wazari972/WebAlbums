/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import java.io.File;
import java.util.List;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.ViewSessionChoix;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.Album_Special;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumAgo;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSelect;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSimple;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumYear;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionPhotoAlbumSize;
import net.wazari.service.exchange.ViewSessionCarnet;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetDisplay;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetEdit;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetSimple;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetSubmit;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.ViewSessionLogin.ViewSessionTempTheme;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.Photo_Special;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionAnAlbum;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplayMassEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSimple;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exchange.ViewSessionTag.Tag_Special;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagCloud;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagDisplay;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagEdit;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagSimple;

/**
 *
 * @author kevin
 */
public class Session implements 
        ServiceSession, ViewSession,
        ViewSessionChoix, ViewSessionPhotoFastEdit,
        ViewSessionAlbum, ViewSessionAlbumDisplay, ViewSessionAlbumAgo, ViewSessionAlbumSimple, ViewSessionAlbumYear, ViewSessionAlbumSelect,
        ViewSessionPhoto, ViewSessionPhotoDisplay, ViewSessionPhotoSimple, ViewSessionAnAlbum,
        ViewSessionTag, ViewSessionTagSimple, ViewSessionTagCloud, ViewSessionTagDisplay,
        ViewSessionCarnet, ViewSessionCarnetDisplay, ViewSessionCarnetSimple {
    public static int stars = 1;
    
    public Theme theme;
    public Integer[] tagAsked = new Integer[0];
    
    public Session(Theme theme) {
        this.theme = theme;
    }
    
    @Override
    public boolean getCompleteChoix() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getWantUnusedTags() {
        return false;
    }
    
    @Override
    public Integer getStarLevel() {
        return stars;
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
                return "kevin";
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
        return true;
    }

    private File tmpdir = new File("/tmp");
    @Override
    public java.io.File getTempDir() {
        return tmpdir;
    }

    private Configuration conf = null;
    @Override
    public Configuration getConfiguration() {
        if (conf != null) return conf;
        conf = new Configuration() {
            private final String SEP = File.separator;
            @Override
            public boolean isPathURL() {
                return false;
            }

            @Override
            public String getImagesPath(boolean withRoot) {
                return getDataPath(withRoot) + "images" + SEP;
            }

            @Override
            public String getFtpPath() {
                return "ftp";
            }

            public String getDataPath(boolean withRoot) {
                return (withRoot ? getRootPath() : "") + "data" + SEP;
            }
            
            @Override
            public String getMiniPath(boolean withRoot) {
                return getDataPath(withRoot) + "miniatures"+ SEP;
            }

            @Override
            public String getRootPath() {
                return "/other/Web/";
            }

            @Override
            public String getBackupPath() {
                return "backup";
            }

            @Override
            public String getTempPath() {
                return "tmp";
            }

            @Override
            public String getConfigFilePath() {
                return "/tmp/conf.xml";
            }

            @Override
            public String getPluginsPath() {
                return "plugins";
            }

            @Override
            public boolean isReadOnly() {
                return true;
            }

            @Override
            public String getSep() {
                return "/";
            }

            @Override
            public boolean wantsProtectDB() {
                return true;
            }
        };
        return conf;
    }

    @Override
    public Integer getThemeId() {
        return theme.getId();
    }

    @Override
    public boolean isRemoteAccess() {
        return false;
    }

    @Override
    public boolean directFileAccess() {
        return true;
    }

    @Override
    public boolean getStatic() {
        return true;
    }

    @Override
    public Theme getTheme() {
        return theme;
    }

    @Override
    public boolean isRootSession() {
        return theme.getId() == 1;
    }

    @Override
    public boolean isSessionManager() {
        return false;
    }

    @Override
    public int getPhotoAlbumSize() {
        return 150;
    }    

    public void setTagAsked(Integer[] tagAsked) {
        this.tagAsked = tagAsked;
    }
    
    @Override
    public Integer[] getTagAsked() {
        return this.tagAsked;
    }

    @Override
    public boolean getWantTagChildren() {
        return false;
    }

    private Integer id = null;
    public void setId(Integer id) {
        this.id = id;
    }
    @Override
    public Integer getId() {
        return id;
    }

    public void setAlbum(Integer album) {
        this.album = album;
    }
    
    private Integer album = null;
    @Override
    public Integer getAlbum() {
        return album;
    }

    @Override
    public Integer getAlbmPage() {
        return null;
    }

    @Override
    public Integer getPage() {
        return null;
    }

    @Override
    public Integer getNbPerYear() {
        return 10;
    }

    @Override
    public ViewSessionPhotoDisplayMassEdit getMassEdit() {
        throw new UnsupportedOperationException("Not supported in FS.");
    }

    @Override
    public boolean getWantTags() {
        return false;
    }
    
    @Override
    public Tag_Special getTagSpecial() {
        return null;
    }

    @Override
    public ViewSessionTagCloud getSessionTagCloud() {
        return this;
    }

    @Override
    public ViewSessionTagSimple getSessionTagSimple() {
        return this;
    }

    @Override
    public ViewSessionTagEdit getSessionTagEdit() {
        return null;
    }

    @Override
    public ViewSessionTagDisplay getSessionTagDisplay() {
        return this;
    }

    @Override
    public ViewSession getVSession() {
        return this;
    }

    @Override
    public ViewSessionAlbumEdit getSessionAlbumEdit() {
        throw new UnsupportedOperationException("Not supported in FS.");
    }

    @Override
    public ViewSessionAlbumSubmit getSessionAlbumSubmit() {
        throw new UnsupportedOperationException("Not supported in FS.");
    }

    @Override
    public ViewSessionAlbumSimple getSessionAlbumSimple() {
        return this;
    }

    @Override
    public ViewSessionAlbumSelect getSessionAlbumSelect() {
        return this;
    }

    @Override
    public ViewSessionAlbumYear getSessionAlbumYear() {
        return this;
    }

    @Override
    public ViewSessionPhotoAlbumSize getPhotoAlbumSizeSession() {
        return null;
    }

    @Override
    public ViewSessionPhotoEdit getSessionPhotoEdit() {
        throw new UnsupportedOperationException("Not supported in FS.");
    }

    @Override
    public ViewSessionPhotoDisplay getSessionPhotoDisplay() {
        return this;
    }

    @Override
    public ViewSessionPhotoSubmit getSessionPhotoSubmit() {
        throw new UnsupportedOperationException("Not supported in FS.");
    }

    @Override
    public ViewSessionPhotoFastEdit getSessionPhotoFastEdit() {
        return this;
    }

    @Override
    public Edit_Action getEditAction() {
        return null;
    }

    @Override
    public Photo_Action getPhotoAction() {
        return null;
    }

    @Override
    public Album_Special getAlbumSpecial() {
        return null;
    }

    @Override
    public Photo_Special getPhotoSpecial() {
        return null;
    }

    @Override
    public ViewSessionAlbumAgo getAgoSession() {
        return this;
    }

    public ViewSessionImages getSessionImage() {
        throw new UnsupportedOperationException("Not supported in FS.");
    }
    @Override
    public ViewSessionAlbumDisplay getSessionAlbumDisplay() {
        return this;
    }

    @Override
    public ViewSessionPhotoSimple getSessionPhotoSimple() {
        return this;
    }

    @Override
    public Choix_Special getChoixSpecial() {
        return null;
    }

    @Override
    public ViewSessionTempTheme getTempThemeSession() {
        return null;
    }

    @Override
    public Integer getYear() {
        return null;
    }

    @Override
    public Integer getMonth() {
        return null;
    }

    @Override
    public Integer getDay() {
        return null;
    }

    @Override
    public boolean getAll() {
        return false;
    }

    @Override
    public boolean getWantMassedit() {
        return false;
    }

    @Override
    public Carnet_Special getCarnetSpecial() {
        return null;
    }

    @Override
    public Carnet_Action getCarnetAction() {
        return null;
    }

    @Override
    public ViewSessionCarnetDisplay getSessionCarnetDisplay() {
        return this;
    }

    @Override
    public ViewSessionCarnetEdit getSessionCarnetEdit() {
        throw new UnsupportedOperationException("Not supported in FS.");
    }

    @Override
    public ViewSessionCarnetSubmit getSessionCarnetSubmit() {
        throw new UnsupportedOperationException("Not supported in FS.");
    }

    @Override
    public ViewSessionCarnetSimple getSessionCarnetSimple() {
        return this;
    }

    @Override
    public Integer getCarnet() {
        return null;
    }

    @Override
    public Integer getCarnetsPage() {
        return null;
    }

    @Override
    public String getDesc() {
        return null;
    }

    public Integer[] tagSet;
    
    @Override
    public Integer[] getTagSet() {
        return tagSet;
    }

    public TagAction tagAction = null;
    @Override
    public TagAction getTagAction() {
        return tagAction;
    }

    @Override
    public Integer getStars() {
        return null;
    }

    @Override
    public Integer getNewStarLevel() {
        return null;
    }

    @Override
    public void setStarLevel(Integer starLevel) {
        throw new UnsupportedOperationException("Not supported in FS.");
    }
}

