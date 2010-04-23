/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.exchange;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.wazari.common.constante.Path;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionTag;

/**
 *
 * @author kevin
 */
public class ViewSessionImpl implements ViewSessionAlbum, ViewSessionConfig, ViewSessionPhoto, ViewSessionTag, ViewSessionImages {
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

    public String getDescr() {
        return getString("descr");
    }

    public String getNom() {
        return getString("nom");
    }

    public String getDate() {
        return getString("date");
    }

    public Integer[] getTags() {
        return castToIntArray(getParamArray("tags"));
    }

    public Boolean getForce() {
        return getBoolean("force");
    }

    public Boolean getSuppr() {
        return getBoolean("suppr");
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
    public String getThemeName() {
        return getSessionObject("themeName", String.class);
    }

    public void setThemeName(String nom) {
        setSessionObject("themeName", nom);
    }

    /** ** **/
    public Integer getThemeId() {
        return getSessionObject("themeId", Integer.class);
    }

    public void setThemeId(Integer newID) {
        setSessionObject("themeId", newID);
    }

    /** ** **/
    public Boolean getDetails() {
        return getSessionObject("details", Boolean.class);
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

    public void setRootSession(boolean asThemeManager) {
        setSessionObject("rootSession", asThemeManager);
    }

    /** ** **/
    public String getUserName() {
        return getSessionObject("userName", String.class);
    }

    public void setUserName(String userName) {
        setSessionObject("userName", userName);
    }

    public boolean isSessionManager() {
        Boolean val =  getBoolean("sessionManager");
        if (val == null) val = false ;
        return val ;
    }

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

    public Boolean getVisible() {
        return getBoolean("visible");
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

    public Boolean getSure() {
        return getBoolean("sure");
    }

    public String getWidth() {
        return getString("width");
    }

    public String getUser() {
        return getString("user");
    }

    public String getDesc() {
        return getString("desc");
    }

    public Boolean getRepresent() {
        return getBoolean("represent");
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

    public Boolean getChk(Integer id) {
        return getBoolean("chk" + id);
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
        return castToIntArray(getParamArray("tagAsked"));
    }

    public ImgMode getImgMode() {
        return getEnum("imgMode", ImgMode.class);
    }

    private String[] getParamArray(String name) {
        return request.getParameterValues(name);
    }

    private Integer[] castToIntArray(String[] from) {
        ArrayList<Integer> ret = new ArrayList<Integer>(from.length);
        for (int i = 0; i < from.length; i++) {
            try {
                ret.add(Integer.parseInt(from[i]));
            } catch (NumberFormatException e) {
            }
        }
        return ret.toArray(new Integer[0]);
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
}
