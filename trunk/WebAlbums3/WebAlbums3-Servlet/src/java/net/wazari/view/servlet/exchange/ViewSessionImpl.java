/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.exchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.common.constante.Path;
import net.wazari.dao.entity.Theme;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay.ViewSessionPhotoDisplayMassEdit.Turn;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSpecial;
import net.wazari.service.exchange.ViewSessionTag;

/**
 *
 * @author kevin
 */
public class ViewSessionImpl implements ViewSessionAlbum,
        ViewSessionConfig,
        ViewSessionPhoto, ViewSessionPhotoDisplay, ViewSessionPhotoEdit, ViewSessionPhotoSpecial,
        ViewSessionTag,
        ViewSessionImages {
    private static final Logger log = Logger.getLogger(ViewSessionImpl.class.getCanonicalName()) ;
    
    private HttpServletRequest request;
    private HttpServletResponse response;
    private static Configuration conf = null;

    public ViewSessionImpl(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        if (conf == null) {
            conf = Path.getConf(context);
        }
        this.request = request;
        this.response = response;

    }

    public String getDesc() {
        return getString("desc");
    }

    public String getNom() {
        return getString("nom");
    }

    public String getDate() {
        return getString("date");
    }

    public Integer[] getTags() {
        return getIntArray("tags");
    }

    public Integer[] getNewTag() {
        return getIntArray("newTag") ;
    }

    public boolean getForce() {
        return "yes".equals(getString("force"));
    }

    public boolean getSuppr() {
        return "Oui je veux supprimer".equals(getString("suppr"));
    }

    public Integer getPage() {
        return getInteger("page");
    }

    public Integer getCount() {
        return getInteger("count");
    }

    public Integer getCountAlbm() {
        return getInteger("countAlbm");
    }

    public Integer getUserAllowed() {
        return getInteger("userAllowed");
    }

    public Special getSpecial() {
        return getEnum("special", Special.class);
    }

    public Action getAction() {
        return getEnum("action", Action.class);
    }

    public String getUserPass() {
        return getString("userPass");
    }

    /** ** **/
    public Integer getThemeId() {
        return getInteger("themeId");
    }
    public void setTheme(Theme enrTheme) {
        setSessionObject("theme", enrTheme);
    }
    public Theme getTheme() {
        return getSessionObject("theme", Theme.class);
    }

    /** ** **/
    public boolean getDetails() {
        Boolean ret = getSessionObject("details", Boolean.class);
        if (ret == null) ret = false ;
        return false ;
    }

    public void setDetails(Boolean newValue) {
        setSessionObject("details", newValue);
    }

    /** ** **/
    public File getTempDir() {
        return getSessionObject("tempDir", File.class);
    }

    public void setTempDir(File temp) {
        setSessionObject("tempDir", temp);
    }

    /** ** **/
    public Integer getUserId() {
        return getSessionObject("userId", Integer.class);
    }

    public void setUserId(Integer userId) {
        setSessionObject("userId", userId);
    }

    /** ** **/
    public EditMode getEditionMode() {
        return getSessionObject("editionMode", EditMode.class);
    }

    public void setEditionMode(EditMode editMode) {
        setSessionObject("editionMode", editMode);
    }

    /** ** **/
    public boolean isRootSession() {
        Boolean val = getSessionObject("rootSession", Boolean.class);
        if (val == null) val = false ;
        return val ;
    }

    public void setRootSession(Boolean rootSession) {
        setSessionObject("rootSession", rootSession);
    }

    /** ** **/
    public String getUserName() {
        return getSessionObject("userName", String.class);
    }

    public void setUserName(String userName) {
        setSessionObject("userName", userName);
    }
    /** ** **/
    public boolean isSessionManager() {
        Boolean ret = getSessionObject("sessionManager", Boolean.class);
        if (ret == null) ret = false ;
        return ret ;
    }

    public void setSessionManager(Boolean sessionManager) {
        setSessionObject("sessionManager", sessionManager);
    }
    /** ** **/

    public Integer getId() {
        return getInteger("id");
    }

    public String getNouveau() {
        return getString("nouveau");
    }

    public Integer getTag() {
        return getInteger("tag");
    }

    public String getLng() {
        return getString("lng");
    }

    public String getLat() {
        return getString("lat");
    }

    public boolean getVisible() {
        return "yes".equals(getString("visible"));
    }

    public String getImportTheme() {
        return getString("importTheme");
    }

    public String getPassword() {
        return getString("password");
    }

    public Integer getType() {
        return getInteger("type");
    }

    public boolean getSure() {
        return "yes".equals(getString("sure"));
    }

    public String getWidth() {
        return getString("width");
    }

    public String getUser() {
        return getString("user");
    }

    public boolean getRepresent() {
        return "y".equals(getString("represent"));
    }

    public Integer getTagPhoto() {
        return getInteger("tagPhoto");
    }

    public Turn getTurn() {
        return getEnum("turn", Turn.class);
    }

    public Integer getAddTag() {
        return getInteger("addTag");
    }

    public boolean getChk(Integer id) {
        return "modif".equals(getString("chk" + id));
    }

    public Integer getRmTag() {
        return getInteger("rmTag");
    }

    public boolean wantsDetails() {
        Boolean val = getSessionObject("details", Boolean.class);
        if (val == null) val = false ;
        return val ;
    }

    public Mode getMode() {
        return getEnum("mode", Mode.class);
    }

    public Integer getAlbum() {
        return getInteger("album");
    }

    public Integer getAlbmCount() {
        return getInteger("albmCount");
    }

    public Integer[] getTagAsked() {
        return getIntArray("tagAsked");
    }

    public ImgMode getImgMode() {
        return getEnum("imgMode", ImgMode.class);
    }

    public Configuration getConfiguration() {
        return conf;
    }

    public void setContentDispositionFilename(String name) {
        response.setHeader("Disposition-Filename", name);
    }

    public void setContentLength(int contentLength) {
        response.setContentLength(contentLength);
    }

    public void setContentType(String type) {
        response.setContentType(type);
    }

    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    /** ** ** ** **/
    
    private Integer getInteger(String name) {
        return getObject(name, Integer.class);
    }

    private String getString(String name) {
        return getObject(name, String.class);
    }

    private Boolean getBoolean(String name) {
        return getObject(name, Boolean.class);
    }

    private <T extends Enum<T>> T getEnum(String value, Class<T> type) {
        return getObject(value, type);
    }

    private <T> T getObject(String name, Class<T> type) {
        T ret = null ;
        String val = request.getParameter(name) ;
        if (val == null) return null ;
        try {
            if (type == String.class) {
                ret = type.cast(val) ;
            } else if (type == Integer.class) {
                ret = type.cast(Integer.parseInt(val));
            } else if (type == Boolean.class) {
                ret = type.cast(Boolean.parseBoolean(val));
            } else if (type.isEnum()) {
                ret = (T) Enum.valueOf((Class)type,val) ;
            } else {
                log.info("Unknown class "+type+" for parameter "+name) ;
            }
        } catch (ClassCastException e) {
            log.info("Can't cast value "+val+" into class "+type) ;
        } catch (NullPointerException e) {
            log.info("NullPointerException with "+val+" for class "+type) ;
        } catch (NumberFormatException e) {
            log.info("NumberFormatException with "+val+" for class "+type) ;
        } catch (IllegalArgumentException e) {
            log.info("IllegalArgumentException with "+val+" for class "+type) ;
        }
        log.info("getObject param:"+name+" type:"+type+" returned "+ret) ;
        return ret ;
    }

    private <T> T getSessionObject(String name, Class<T> type) {
        T ret = type.cast(request.getSession().getAttribute(name));
        if (ret == null) {
            ret = getObject(name, type) ;
        }
        log.info("getSessionObject param:"+name+" type:"+type+" returned "+ret) ;
        return ret;
    }

    private void setSessionObject(String key, Object val) {
        log.info("setSessionObject param:"+key+" val:"+val) ;
        request.getSession().setAttribute(key, val);
    }

    public int getAlbumSize() {
        return getConfiguration().getAlbumSize() ;
    }

    public int getPhotoSize() {
        return getConfiguration().getPhotoSize() ;
    }

    private Integer[] getIntArray(String key) {
        return castToIntArray(getParamArray(key));
    }

     private String[] getParamArray(String name) {
        String[] ret = request.getParameterValues(name);
        log.info("getParamArray param:"+name+" returned "+Arrays.toString(ret)) ;
        return ret ;
    }

    private Integer[] castToIntArray(String[] from) {
        if (from == null) return null ;
        ArrayList<Integer> ret = new ArrayList<Integer>(from.length);
        for (int i = 0; i < from.length; i++) {
            try {
                ret.add(Integer.parseInt(from[i]));
            } catch (NumberFormatException e) {
            }
        }
        return ret.toArray(new Integer[0]);
    }

    @Override
    public boolean isAuthenticated() {
        return getUserPrincipal() != null ;
    }

    @Override
    public Principal getUserPrincipal() {
        return request.getUserPrincipal() ;
    }

    @Override
    public boolean authenticate() {
        try {
            return request.authenticate(response);
        } catch (IOException e) {

        } catch (ServletException e) {
            
        }
        return false ;
    }

    @Override
    public void login(String user, String passwd) {
        try {
            request.login(user, passwd);
        } catch (ServletException ex) {
            Logger.getLogger(ViewSessionImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ViewSessionPhotoDisplayMassEdit getMassEdit() {
        return (ViewSessionPhotoDisplayMassEdit) this ;
    }

}
