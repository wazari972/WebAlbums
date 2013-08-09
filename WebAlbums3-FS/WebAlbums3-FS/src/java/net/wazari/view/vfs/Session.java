/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit;
import net.wazari.service.exchange.ViewSessionTag;

/**
 *
 * @author kevin
 */
public class Session implements ViewSession, ViewSessionTag, ViewSessionAlbum, ViewSessionPhotoDisplay, ViewSessionPhoto, ViewSessionImages, ViewSessionPhotoFastEdit {
    public static int stars = 1;
    
    public Theme theme;
    public Integer[] tagAsked = new Integer[0];
    private Integer[] tagSet;
    
    public Session(Theme theme) {
        this.theme = theme;
    }
    
    //Override
    public boolean getCompleteChoix() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    /*public ViewSession.Special getSpecial() {
        return null;
    }

    //Override
    public ViewSession.Action getAction() {
        return null;
    }*/
    
    //Override
    public boolean getWantUnusedTags() {
        return false;
    }
    
    //Override
    public Integer getNewStarLevel() {
        return null;
    }
    
    //Override
    public Integer getStarLevel() {
        return stars;
    }
    
    //Override
    public Utilisateur getUser() {
        return new Utilisateur() {

            //Override
            public List<Album> getAlbumList() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            //Override
            public String getNom() {
                return "kevin";
            }

            //Override
            public void setAlbumList(List<Album> albumList) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            //Override
            public void setId(Integer id) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            //Override
            public void setNom(String nom) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            //Override
            public Integer getId() {
                return 1;
            }
        };
    }

    //Override
    public boolean isAuthenticated() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private File tmpdir = new File("/tmp");
    //Override
    public java.io.File getTempDir() {
        return tmpdir;
    }

    private Configuration conf = null;
    //Override
    public Configuration getConfiguration() {
        if (conf != null) return conf;
        conf = new Configuration() {
            private final String SEP = File.separator;
            //Override
            public boolean isPathURL() {
                return false;
            }

            //Override
            public String getImagesPath(boolean withRoot) {
                return getDataPath(withRoot) + "images" + SEP;
            }

            //Override
            public String getFtpPath() {
                return "ftp";
            }

            public String getDataPath(boolean withRoot) {
                return (withRoot ? getRootPath() : "") + "data" + SEP;
            }
            
            //Override
            public String getMiniPath(boolean withRoot) {
                return getDataPath(withRoot) + "miniatures"+ SEP;
            }

            //Override
            public String getRootPath() {
                return "/home/kevin/vayrac/";
            }

            //Override
            public String getBackupPath() {
                return "backup";
            }

            //Override
            public String getTempPath() {
                return "tmp";
            }

            //Override
            public String getConfigFilePath() {
                return "/tmp/conf.xml";
            }

            //Override
            public String getPluginsPath() {
                return "plugins";
            }

            //Override
            public boolean isReadOnly() {
                return true;
            }

            //Override
            public String getSep() {
                return "/";
            }

            //Override
            public boolean wantsProtectDB() {
                return true;
            }
        };
        return conf;
    }

    //Override
    public Integer getThemeId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public boolean isRemoteAccess() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public boolean directFileAccess() {
        return true;
    }

    //Override
    public void setDirectFileAccess(boolean access) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public void setStatic(boolean statik) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public boolean getStatic() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public Theme getTheme() {
        return theme;
    }

    //Override
    public boolean isRootSession() {
        return theme.getId() == 1;
    }

    //Override
    public boolean isSessionManager() {
        return false;
    }

    //Override
    public int getPhotoAlbumSize() {
        return 150;
    }    

    public void setTagAsked(Integer[] tagAsked) {
        this.tagAsked = tagAsked;
    }
    
    //Override
    public Integer[] getTagAsked() {
        return this.tagAsked;
    }

    //Override
    public boolean getWantTagChildren() {
        return false;
    }

    private Integer id = null;
    public void setId(Integer id) {
        this.id = id;
    }
    //Override
    public Integer getId() {
        return id;
    }

    //Override
    public Integer getTagPhoto() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public Tag_Mode getMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAlbum(Integer album) {
        this.album = album;
    }
    
    private Integer album = null;
    //Override
    public Integer getAlbum() {
        return album;
    }

    //Override
    public Integer getAlbmPage() {
        return null;
    }

    //Override
    public Integer getPage() {
        return null;
    }

    //Override
    public Integer getNbPerYear() {
        return 10;
    }

    //Override
    public void setPhotoAlbumSize(int size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public ViewSessionPhotoDisplayMassEdit getMassEdit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }
    private Integer borderWidth;
    //Override
    public Integer getBorderWidth() {
        return borderWidth;
    }

    private String color;
    public void setBorderColor(String color) {
        this.color = color;
    }
    //Override
    public String getBorderColor() {
        return color;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
    private Integer width = 5;
    //Override
    public Integer getWidth() {
        return width;
    }

    //Override
    public ImgMode getImgMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public void setContentDispositionFilename(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public void setContentLength(int contentLength) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public void setContentType(String type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public void redirect(String filepath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Override
    public String getDesc() {
        return null;
    }

    public void setTagSet(Integer[] tagSet) {
        this.tagSet = tagSet;
    }
    
    //Override
    public Integer[] getTagSet() {
        return tagSet;
    }

    private TagAction tagAction = null;
    //Override
    public TagAction getTagAction() {
        return tagAction;
    }
    
    public void setTagAction(TagAction action) {
        this.tagAction = action;
    }

    //Override
    public Integer getStars() {
        return null;
    }

    //Override
    public boolean getWantTags() {
        return false;
    }

    //Override
    public void setStarLevel(Integer starLevel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public ViewSessionTag.Special getTagSpecial() {
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
    public Action getAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Action getPhotoAction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionAlbum.Special getAlbumSpecial() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ViewSessionPhoto.Special getPhotoSpecial() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

