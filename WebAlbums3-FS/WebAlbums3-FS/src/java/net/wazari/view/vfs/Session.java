/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.SessionConfig;
import net.wazari.service.exchange.ViewSession.ViewSessionChoix;
import net.wazari.service.exchange.ViewSession.ViewSessionTheme;
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
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exchange.ViewSessionDatabase;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.ViewSessionImages.ImgMode;
import net.wazari.service.exchange.ViewSessionLogin;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.Photo_Special;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplayMassEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit.TagAction;
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
        ViewSessionChoix,
        ViewSessionLogin, ViewSessionLogin.ViewSessionTempTheme,
        ViewSessionAlbum, ViewSessionAlbumDisplay, ViewSessionAlbumEdit, ViewSessionAlbumSubmit, ViewSessionAlbumAgo, ViewSessionAlbumSimple, ViewSessionAlbumYear, ViewSessionAlbumSelect, ViewSessionPhotoAlbumSize,
        ViewSessionConfig,
        ViewSessionPhoto, ViewSessionPhotoDisplay, ViewSessionPhotoEdit, ViewSessionPhotoSubmit, ViewSessionPhotoDisplayMassEdit, ViewSessionPhotoFastEdit, ViewSessionPhotoSimple, ViewSessionPhoto.ViewSessionAnAlbum,
        ViewSessionTag, ViewSessionTagSimple, ViewSessionTagCloud, ViewSessionTagDisplay, ViewSessionTagEdit,
        ViewSessionImages, 
        ViewSessionCarnet, ViewSessionCarnet.ViewSessionCarnetDisplay, ViewSessionCarnet.ViewSessionCarnetEdit, ViewSessionCarnet.ViewSessionCarnetSubmit, ViewSessionCarnet.ViewSessionCarnetSimple,
        ViewSessionDatabase, ViewSessionTheme, SessionConfig {
    public static int stars = 1;
    
    public Theme theme;
    public Integer[] tagAsked = new Integer[0];
    private Integer[] tagSet;
    
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
    public Integer getNewStarLevel() {
        return null;
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
        throw new UnsupportedOperationException("Not supported yet.");
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
                return "/home/kevin/vayrac/";
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isRemoteAccess() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean directFileAccess() {
        return true;
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

    @Override
    public Integer getTagPhoto() {
        throw new UnsupportedOperationException("Not supported yet.");
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
    public void setPhotoAlbumSize(int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ViewSessionPhotoDisplayMassEdit getMassEdit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }
    private Integer borderWidth;
    @Override
    public Integer getBorderWidth() {
        return borderWidth;
    }

    private String color;
    public void setBorderColor(String color) {
        this.color = color;
    }
    @Override
    public String getBorderColor() {
        return color;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
    private Integer width = 5;
    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public ImgMode getImgMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setContentDispositionFilename(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setContentLength(int contentLength) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setContentType(String type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void redirect(String filepath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getDesc() {
        return null;
    }

    public void setTagSet(Integer[] tagSet) {
        this.tagSet = tagSet;
    }
    
    @Override
    public Integer[] getTagSet() {
        return tagSet;
    }

    private TagAction tagAction = null;
    @Override
    public TagAction getTagAction() {
        return tagAction;
    }
    
    public void setTagAction(TagAction action) {
        this.tagAction = action;
    }

    @Override
    public Integer getStars() {
        return null;
    }

    @Override
    public boolean getWantTags() {
        return false;
    }

    @Override
    public void setStarLevel(Integer starLevel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Tag_Special getTagSpecial() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionTagCloud getSessionTagCloud() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionTagSimple getSessionTagSimple() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionTagEdit getSessionTagEdit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionTagDisplay getSessionTagDisplay() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSession getVSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionAlbumEdit getEditSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionAlbumSubmit getSubmitSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionAlbumSimple getSimpleSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionAlbumSelect getSelectSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionAlbumYear getYearSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionPhotoAlbumSize getPhotoAlbumSizeSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionPhotoEdit getSessionPhotoEdit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionPhotoDisplay getSessionPhotoDisplay() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionPhotoSubmit getSessionPhotoSubmit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionPhotoFastEdit getSessionPhotoFastEdit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Edit_Action getEditAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Photo_Action getPhotoAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Album_Special getAlbumSpecial() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Photo_Special getPhotoSpecial() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionAlbumAgo getAgoSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ViewSessionImages getSessionImage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public ViewSessionAlbumDisplay getDisplaySession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionPhotoSimple getSessionPhotoSimple() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ViewSessionAlbumSelect getSessionAlbumSelect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Choix_Special getChoixSpecial() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Login_Action getLoginAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTheme(Theme enrTheme) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSessionManager(Boolean sessionManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRootSession(Boolean asThemeManager) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setUser(Utilisateur enrUser) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void login(String user, String passwd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getUserName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getUserPass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean dontRedirect() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getwantManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTempTheme(Theme enrTheme) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionTempTheme getTempThemeSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer[] getTags() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getForce() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getSuppr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getUserAllowed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNewTheme() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getYear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getMonth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getDay() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getAll() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getNewPhotoAlbumSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Config_Special getConfigSpecial() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Config_Action getConfigAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getMinor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNouveau() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getTag() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLng() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getVisible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getImportTheme() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getParentTag() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer[] getSonTags() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getBirthdate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getContact() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getWantMassedit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getRepresent() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDroit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer[] getNewTag() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getThemeBackground() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getThemePicture() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Turn getTurn() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getChk(Integer id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer[] getAddTags() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getRmTag() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Carnet_Special getCarnetSpecial() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Carnet_Action getCarnetAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionCarnetDisplay getDisplayCarnetSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionCarnetEdit getEditCarnetSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionCarnetSubmit getSubmitCarnetSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionCarnetSimple getSimpleCarnetSession() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getCarnet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getCarnetsPage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCarnetText() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Integer> getCarnetPhoto() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer getCarnetRepr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Integer> getCarnetAlbum() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Database_Action getDatabaseAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SessionConfig getSessionConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

