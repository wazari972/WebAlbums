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
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession.Action;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Special;
import net.wazari.service.exchange.ViewSessionLogin;
import net.wazari.service.exchange.ViewSessionSession;
import net.wazari.service.exchange.ViewSessionAlbum;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.service.exchange.ViewSessionConfig;
import net.wazari.service.exchange.ViewSessionImages;
import net.wazari.service.exchange.ViewSessionMaint;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay.ViewSessionPhotoDisplayMassEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay.ViewSessionPhotoDisplayMassEdit.Turn;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSpecial;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.ViewSessionTag;

/**
 *
 * @author kevin
 */
public class ViewSessionImpl implements
        ServiceSession,
        ViewSessionSession,
        ViewSessionLogin,
        ViewSessionAlbum, ViewSessionAlbumDisplay, ViewSessionAlbumEdit, ViewSessionAlbumSubmit,
        ViewSessionConfig,
        ViewSessionPhoto, ViewSessionPhotoDisplay, ViewSessionPhotoEdit, ViewSessionPhotoSubmit, ViewSessionPhotoSpecial,
        ViewSessionTag,
        ViewSessionImages, ViewSessionPhotoDisplayMassEdit,
        ViewSessionMaint{

    private static final Logger log = Logger.getLogger(ViewSessionImpl.class.getCanonicalName());
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

    @Override
    public String getDesc() {
        return getString("desc");
    }

    @Override
    public String getNom() {
        return getString("nom");
    }

    @Override
    public String getDate() {
        return getString("date");
    }

    @Override
    public Integer[] getTags() {
        return getIntArray("tags");
    }

    @Override
    public Integer[] getNewTag() {
        return getIntArray("newTag");
    }

    @Override
    public boolean getForce() {
        return "yes".equals(getString("force"));
    }

    @Override
    public boolean getSuppr() {
        return "Oui je veux supprimer".equals(getString("suppr"));
    }

    @Override
    public Integer getPage() {
        return getInteger("page");
    }

    @Override
    public Integer getCount() {
        return getInteger("count");
    }

    @Override
    public Integer getCountAlbm() {
        return getInteger("countAlbm");
    }

    @Override
    public Integer getUserAllowed() {
        return getInteger("userAllowed");
    }

    @Override
    public Special getSpecial() {
        return getEnum("special", Special.class);
    }

    @Override
    public Action getAction() {
        return getEnum("action", Action.class);
    }

    @Override
    public String getUserPass() {
        return getString("userPass");
    }

    /** ** **/
    @Override
    public Integer getThemeId() {
        return getInteger("themeId");
    }

    @Override
    public void setTheme(Theme enrTheme) {
        setSessionObject("theme", enrTheme);
    }

    @Override
    public Theme getTheme() {
        return getSessionObject("theme", Theme.class);
    }

    /** ** **/
    @Override
    public boolean getDetails() {
        Boolean ret = getSessionObject("details", Boolean.class);
        if (ret == null) {
            ret = false;
        }
        return false;
    }

    @Override
    public void setDetails(Boolean newValue) {
        setSessionObject("details", newValue);
    }

    /** ** **/
    @Override
    public File getTempDir() {
        return getSessionObject("tempDir", File.class);
    }

    @Override
    public void setTempDir(File temp) {
        setSessionObject("tempDir", temp);
    }

    /** ** **/
    @Override
    public Integer getUserId() {
        return getSessionObject("userId", Integer.class);
    }

    @Override
    public void setUserId(Integer userId) {
        setSessionObject("userId", userId);
    }

    /** ** **/
    @Override
    public EditMode getEditionMode() {
        return getSessionObject("editionMode", EditMode.class);
    }

    @Override
    public void setEditionMode(EditMode editMode) {
        setSessionObject("editionMode", editMode);
    }

    /** ** **/
    @Override
    public boolean isRootSession() {
        Boolean val = getSessionObject("rootSession", Boolean.class);
        if (val == null) {
            val = false;
        }
        return val;
    }

    @Override
    public void setRootSession(Boolean rootSession) {
        setSessionObject("rootSession", rootSession);
    }

    /** ** **/
    @Override
    public String getUserName() {
        return getSessionObject("userName", String.class);
    }

    @Override
    public void setUserName(String userName) {
        setSessionObject("userName", userName);
    }

    /** ** **/
    @Override
    public boolean isSessionManager() {
        Boolean ret = getSessionObject("sessionManager", Boolean.class);
        if (ret == null) {
            ret = false;
        }
        return ret;
    }

    @Override
    public void setSessionManager(Boolean sessionManager) {
        setSessionObject("sessionManager", sessionManager);
    }

    /** ** **/
    @Override
    public Integer getId() {
        return getInteger("id");
    }

    @Override
    public String getNouveau() {
        return getString("nouveau");
    }

    @Override
    public Integer getTag() {
        return getInteger("tag");
    }

    @Override
    public String getLng() {
        return getString("lng");
    }

    @Override
    public String getLat() {
        return getString("lat");
    }

    @Override
    public boolean getVisible() {
        return "yes".equals(getString("visible"));
    }

    @Override
    public String getImportTheme() {
        return getString("importTheme");
    }

    @Override
    public String getPassword() {
        return getString("password");
    }

    @Override
    public Integer getType() {
        return getInteger("type");
    }

    @Override
    public String getWidth() {
        return getString("width");
    }

    @Override
    public String getUser() {
        return getString("user");
    }

    @Override
    public boolean getRepresent() {
        return "y".equals(getString("represent"));
    }

    @Override
    public Integer getTagPhoto() {
        return getInteger("tagPhoto");
    }

    @Override
    public Turn getTurn() {
        return getEnum("turn", Turn.class);
    }

    @Override
    public Integer getAddTag() {
        return getInteger("addTag");
    }

    @Override
    public boolean getChk(Integer id) {
        return "modif".equals(getString("chk" + id));
    }

    @Override
    public Integer getRmTag() {
        return getInteger("rmTag");
    }

    @Override
    public boolean wantsDetails() {
        Boolean val = getSessionObject("details", Boolean.class);
        if (val == null) {
            val = false;
        }
        return val;
    }

    @Override
    public Mode getMode() {
        return getEnum("mode", Mode.class);
    }

    @Override
    public Integer getAlbum() {
        return getInteger("album");
    }

    @Override
    public Integer getAlbmCount() {
        return getInteger("albmCount");
    }

    @Override
    public Integer[] getTagAsked() {
        return getIntArray("tagAsked");
    }

    @Override
    public ImgMode getImgMode() {
        return getEnum("mode", ImgMode.class);
    }

    @Override
    public Configuration getConfiguration() {
        return conf;
    }

    @Override
    public void setContentDispositionFilename(String name) {
        response.setHeader("Disposition-Filename", name);
    }

    @Override
    public void setContentLength(int contentLength) {
        response.setContentLength(contentLength);
    }

    @Override
    public void setContentType(String type) {
        response.setContentType(type);
    }

    @Override
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
        T ret = null;
        String val = request.getParameter(name);
        if (val == null) {
            return null;
        }
        try {
            if (type == String.class) {
                ret = type.cast(val);
            } else if (type == Integer.class) {
                ret = type.cast(Integer.parseInt(val));
            } else if (type == Boolean.class) {
                ret = type.cast(Boolean.parseBoolean(val));
            } else if (type.isEnum()) {
                ret = (T) Enum.valueOf((Class) type, val);
            } else {
                log.log(Level.INFO, "Unknown class {0} for parameter {1}", new Object[]{type, name});
            }
        } catch (ClassCastException e) {
            log.log(Level.INFO, "Can''t cast value {0} into class {1}", new Object[]{val, type});
        } catch (NullPointerException e) {
            log.log(Level.INFO, "NullPointerException with {0} for class {1}", new Object[]{val, type});
        } catch (NumberFormatException e) {
            log.log(Level.INFO, "NumberFormatException with {0} for class {1}", new Object[]{val, type});
        } catch (IllegalArgumentException e) {
            log.log(Level.INFO, "IllegalArgumentException with {0} for class {1}", new Object[]{val, type});
        }
        log.log(Level.INFO, "getObject param:{0} type:{1} returned {2}", new Object[]{name, type, ret});
        return ret;
    }

    private <T> T getSessionObject(String name, Class<T> type) {
        T ret = type.cast(request.getSession().getAttribute(name));
        if (ret == null) {
            ret = getObject(name, type);
        }
        log.log(Level.INFO, "getSessionObject param:{0} type:{1} returned {2}", new Object[]{name, type, ret});
        return ret;
    }

    private void setSessionObject(String key, Object val) {
        log.log(Level.INFO, "setSessionObject param:{0} val:{1}", new Object[]{key, val});
        request.getSession().setAttribute(key, val);
    }

    @Override
    public int getAlbumSize() {
        return getConfiguration().getAlbumSize();
    }

    @Override
    public int getPhotoSize() {
        return getConfiguration().getPhotoSize();
    }

    private Integer[] getIntArray(String key) {
        return castToIntArray(getParamArray(key));
    }

    private String[] getParamArray(String name) {
        String[] ret = request.getParameterValues(name);
        log.log(Level.INFO, "getParamArray param:{0} returned {1}", new Object[]{name, Arrays.toString(ret)});
        return ret;
    }

    private Integer[] castToIntArray(String[] from) {
        if (from == null) {
            return new Integer[]{};
        }
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
        return getUserPrincipal() != null;
    }

    @Override
    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
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
        return (ViewSessionPhotoDisplayMassEdit) this;
    }

    @Override
    public MaintAction getMaintAction() {
        return getObject("action", MaintAction.class);
    }

    @Override
    public String getParam() {
        return getObject("param", String.class);
    }

    @Override
    public String getValue() {
        return getObject("value", String.class);
    }
}
