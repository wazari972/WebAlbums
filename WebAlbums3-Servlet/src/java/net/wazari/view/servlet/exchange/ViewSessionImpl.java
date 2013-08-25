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
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.service.exchange.*;
import net.wazari.service.exchange.ViewSession.Edit_Action;
import net.wazari.service.exchange.ViewSession.SessionConfig;
import net.wazari.service.exchange.ViewSession.ViewSessionChoix;
import net.wazari.service.exchange.ViewSession.ViewSessionTheme;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumAgo;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumDisplay;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumEdit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSelect;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSimple;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumSubmit;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionAlbumYear;
import net.wazari.service.exchange.ViewSessionAlbum.ViewSessionPhotoAlbumSize;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetDisplay;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetEdit;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetSimple;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetSubmit;
import net.wazari.service.exchange.ViewSessionImages.ImgMode;
import net.wazari.service.exchange.ViewSessionLogin.ViewSessionTempTheme;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionAnAlbum;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplay;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplayMassEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoDisplayMassEdit.Turn;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoFastEdit;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSimple;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionPhotoSubmit;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagCloud;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagDisplay;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagEdit;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagSimple;
import net.wazari.view.servlet.DispatcherBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
public class ViewSessionImpl implements
        ServiceSession, ViewSession,
        ViewSessionChoix,
        ViewSessionLogin, ViewSessionLogin.ViewSessionTempTheme,
        ViewSessionAlbum, ViewSessionAlbumDisplay, ViewSessionAlbumEdit, ViewSessionAlbumSubmit, ViewSessionAlbumAgo, ViewSessionAlbumSimple, ViewSessionAlbumYear, ViewSessionAlbumSelect, ViewSessionPhotoAlbumSize,
        ViewSessionConfig,
        ViewSessionPhoto, ViewSessionPhotoDisplay, ViewSessionPhotoEdit, ViewSessionPhotoSubmit, ViewSessionPhotoDisplayMassEdit, ViewSessionPhotoFastEdit, ViewSessionPhotoSimple, ViewSessionAnAlbum,
        ViewSessionTag, ViewSessionTagSimple, ViewSessionTagCloud, ViewSessionTagDisplay, ViewSessionTagEdit,
        ViewSessionImages, 
        ViewSessionCarnet, ViewSessionCarnetDisplay, ViewSessionCarnetEdit, ViewSessionCarnetSubmit, ViewSessionCarnetSimple,
        ViewSessionDatabase, DispatcherBean.ViewSessionDispatcher, ViewSessionTheme, SessionConfig
        {

    private static final Logger log = LoggerFactory.getLogger(ViewSessionImpl.class.getCanonicalName());
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    private Integer DEFAULT_PHOTOALBUM_SIZE = 15;
    private Theme tempTheme = null;
    
    public ViewSessionImpl(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        this.request = request;
        this.response = response;
    }

    @Override
    public String getBirthdate() {
        return getString("birthdate");
    }
    
    @Override
    public String getContact() {
        return getString("contact");
    }
    
    @Override
    public String getDesc() {
        return getString("desc");
    }
    
    @Override
    public String getCarnetText() {
        return getString("carnetText");
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
    public int getNewTheme() {
        return getInteger("newTheme");
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
        String suppr = getString("suppr") ;
        return "Oui je veux supprimer cette photo".equals(suppr) ||
               "Oui je veux supprimer cet album".equals(suppr)  ||
               "Oui je veux supprimer ce carnet".equals(suppr);
    }

    @Override
    public Integer getPage() {
        return getInteger("page");
    }
    
    @Override
    public Integer getNbPerYear() {
        return getInteger("nbPerYear");
    }
    
    @Override
    public Integer getUserAllowed() {
        return getInteger("user");
    }

    @Override
    public String getUserPass() {
        return getString("userPass");
    }

    @Override
    public String getUserName() {
        return getString("userName") ;
    }
    
    @Override
    public Boolean dontRedirect() {
        return getBoolean("dontRedirect") ;
    }
    @Override
    public boolean directFileAccess() {
        String direct = getString("directFileAccess");
        if (direct == null) {
            direct = getSessionObject("directFileAccess", String.class);
        }
        return "y".equals(direct);
    }
    
    @Override
    public void setDirectFileAccess(boolean access) {
        setSessionObject("directFileAccess", access ? "y" : "n");
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
    public void setTempTheme(Theme enrTheme) {
        this.tempTheme = enrTheme;
    }
    
    @Override
    public Theme getTheme() {
        if (this.tempTheme != null) {
            return this.tempTheme;
        }
        return getSessionObject("theme", Theme.class);
    }

    /** ** **/

    @Override
    public File getTempDir() {
        return getSessionObject("tempDir", File.class);
    }

    /** ** **/
    @Override
    public Utilisateur getUser() {
        return getSessionObject("user", Utilisateur.class);
    }
    
    @Override
    public void setUser(Utilisateur enrUser) {
        setSessionObject("user", enrUser);
    }

    /** ** **/
    
    @Override
    public Boolean getwantManager() {
        Boolean want = getBoolean("wantManager")  ;
        if (want == null) {
            want = false;
        }
        return want;
    }
    
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
    public Integer getCarnet() {
        return getInteger("carnet");
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
    public Integer getNewStarLevel() {
        return getInteger("newStarLevel");
    }
    
    @Override
    public Integer getStarLevel() {
        Integer ret = getSessionObject("starLevel", Integer.class);
        
        if (ret == null) {
            ret = 1;
        }
        
        return ret;
    }
    
    @Override
    public void setStarLevel(Integer starLevel) {
        setSessionObject("starLevel", starLevel);
    }

    
    @Override
    public Integer getStars() {
        return getInteger("stars");
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
        return "y".equals(getString("visible"));
    }
    
    @Override
    public boolean getMinor() {
        return "y".equals(getString("minor"));
    }
    
    @Override
    public boolean getCompleteChoix() {
        return "y".equals(getString("complete"));
    }

    @Override
    public String getImportTheme() {
        return getString("importTheme");
    }

    @Override
    public Integer getType() {
        return getInteger("type");
    }

    @Override
    public Integer getWidth() {
        return getInteger("width");
    }

    @Override
    public boolean getRepresent() {
        return "y".equals(getString("represent"));
    }

    @Override
    public boolean getThemeBackground() {
        return "y".equals(getString("themeBackground"));
    }
    
    @Override
    public boolean getThemePicture() {
        return "y".equals(getString("themePicture"));
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
    public Integer[] getAddTags() {
        return getIntArray("addTag");
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
    public Integer getAlbum() {
        return getInteger("album");
    }

    @Override
    public Integer getAlbmPage() {
        return getInteger("albmPage");
    }

    @Override
    public Integer[] getTagSet() {
        return getIntArray("tagSet");
    }
    
    @Override
    public Integer[] getTagAsked() {
        return getIntArray("tagAsked");
    }

    @Override
    public boolean getWantTagChildren() {
        return getString("wantTagChildren") != null;
    }

    @Override
    public boolean getWantUnusedTags() {
        return getString("wantUnusedTags") != null;
    }
    
    @Override
    public ImgMode getImgMode() {
        return getEnum("mode", ImgMode.class);
    }

    @Override
    public Configuration getConfiguration() {
        return ConfigurationXML.getConf();
    }

    @Override
    public void setContentDispositionFilename(String name) {
        response.addHeader("Content-Disposition", "filename=\""+name+"\"");
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

    @Override
    public Integer getParentTag() {
        return getInteger("parentTag") ;
    }

    @Override
    public Integer[] getSonTags() {
        return getIntArray("sonTag") ;
    }
    
    private Set<Integer> splitInt(String in)  {
        if (in == null || in.length() == 1) {
            return new HashSet<Integer>();
        }
        
        Set<Integer> out = new HashSet<Integer>();
        for (String str : in.split("-")) {
            try {
                out.add(Integer.parseInt(str));
            } catch (NumberFormatException e) {
                log.warn("Invalide number during split int: {}", str);
            }
                
        }
        return out ;
    }
    
    @Override
    public Set<Integer> getCarnetPhoto() {
        return splitInt(getString("carnetPhoto")) ;
    }
    
    @Override
    public Set<Integer> getCarnetAlbum() {
        return splitInt(getString("carnetAlbum")) ;
    }
    
    @Override
    public Integer getCarnetRepr() {
        return getInteger("carnetRepr") ;
    }

    @Override
    public Integer getBorderWidth() {
        return getInteger("borderWidth");
    }

    @Override
    public String getBorderColor() {
        return getString("borderColor") ;
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

    private Integer[] getIntArray(String key) {
        return castToIntArray(getParamArray(key));
    }

    private String[] getParamArray(String name) {
        String[] ret = request.getParameterValues(name);
        log.info( "getParamArray param:{} returned {}", new Object[]{name, Arrays.toString(ret)});
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
            log.warn("ServletException", ex);
        }
    }

    @Override
    public ViewSessionPhotoDisplayMassEdit getMassEdit() {
        return (ViewSessionPhotoDisplayMassEdit) this;
    }
    
    @Override
    public TagAction getTagAction() {
        return getObject("tagAction", TagAction.class);
    }
    
    @Override
    public Integer getCarnetsPage() {
        return getInteger("carnetsPage") ;
    }

    @Override
    public int getPhotoAlbumSize() {
        Integer size = getInteger("photoAlbumSize");
        
        if (size == null) {
            size = getSessionObject("photoAlbumSize", Integer.class);
        }
        if (size == null) {
            size = DEFAULT_PHOTOALBUM_SIZE;
        }
        
        return size;
    }
    
    @Override
    public void setPhotoAlbumSize(int size) {
        setSessionObject("photoAlbumSize", size);
    }
    
    
    @Override
    public boolean getStatic() {
        return "y".equals(getSessionObject("static", String.class));
    }
    
    @Override
    public void setStatic(boolean statik) {
        setSessionObject("static", statik ? "y" : "n");
    }
    
    private void setSessionObject(String string, Object value) {
        setSessionObject(string, value, request.getSession());
    }

    private <T> T getObject(String string, Class<T> aClass) {
        return getObject(string, aClass, request) ;
    }

    private <T> T getSessionObject(String key, Class<T> aClass) {
        return getSessionObject(key, aClass, request.getSession(), request) ;
    }

    private static <T> T getObject(String name, Class<T> type, HttpServletRequest request) {
        T ret = null;
        String val = request.getParameter(name);

        try {
            if (val == null) {
                ret = null ;
            } else if (type == String.class) {
                ret = type.cast(val);
            } else if (type == Integer.class) {
                ret = type.cast(Integer.parseInt(val));
            } else if (type == Boolean.class) {
                ret = type.cast(Boolean.parseBoolean(val));
            } else if (type.isEnum()) {
                ret = (T) Enum.valueOf((Class) type, val);
            } else {
                log.warn( "Unknown class {} for parameter {}", new Object[]{type, name});
            }
        } catch (ClassCastException e) {
            log.info( "Can''t cast value {} into class {}", new Object[]{val, type});
        } catch (NullPointerException e) {
            log.info( "NullPointerException with {} for class {} ({})", new Object[]{val, type, name});
        } catch (NumberFormatException e) {
            log.info( "NumberFormatException with  '{}' for class {} ({})", new Object[]{val, type, name});
        } catch (IllegalArgumentException e) {
            log.info( "IllegalArgumentException with {} for class {}", new Object[]{val, type});
        }
        log.debug( "getObject param:{} type:{} returned {}", new Object[]{name, type, ret});
        return ret;
    }

    private static <T> T getSessionObject(String name, Class<T> type, HttpSession session, HttpServletRequest request) {
        T ret = type.cast(session.getAttribute(name));
        if (ret == null && request != null) {
            ret = getObject(name, type, request);
        }
        log.debug( "getSessionObject param:{} type:{} returned {}", new Object[]{name, type, ret});
        return ret;
    }

    private static void setSessionObject(String key, Object val, HttpSession session) {
        log.info( "setSessionObject param:{} val:{}", new Object[]{key, val});
        session.setAttribute(key, val);
    }

    @Override
    public String getDroit() {
        return getString("user") ;
    }

    @Override
    public boolean isRemoteAccess() {
        return !request.getLocalAddr().equals(request.getRemoteHost()) ;
    }

    @Override
    public void redirect(String filepath) {
        try {
            response.sendRedirect(filepath);
        } catch (IOException ex) {
            log.error("IOException", ex);
        }
    }

    @Override
    public boolean getWantTags() {
        return getObject("wantTags", String.class) != null;
    }

    @Override
    public Integer getYear() {
        return getInteger("year");
    }

    @Override
    public Integer getMonth() {
        return getInteger("month");
    }

    @Override
    public Integer getDay() {
        return getInteger("day");
    }

    @Override
    public boolean getAll() {
        return getString("all") != null && "y".equals(getString("all"));
    }

    @Override
    public Login_Action getLoginAction() {
        return getEnum("action", Login_Action.class);
    }

    @Override
    public ViewSession getVSession() {
        return this;
    }

    @Override
    public Album_Special getAlbumSpecial() {
        return getEnum("special", Album_Special.class);
    }

    @Override
    public Edit_Action getEditAction() {
        return getEnum("action", Edit_Action.class);
    }

    @Override
    public ViewSessionAlbumEdit getSessionAlbumEdit() {
        return this;
    }

    @Override
    public ViewSessionAlbumSubmit getSessionAlbumSubmit() {
        return this;
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
    public ViewSessionAlbumAgo getAgoSession() {
        return this;
    }

    @Override
    public ViewSessionAlbumDisplay getSessionAlbumDisplay() {
        return this;
    }

    @Override
    public ViewSessionAlbumYear getSessionAlbumYear() {
        return this;
    }

    @Override
    public ViewSessionPhotoAlbumSize getPhotoAlbumSizeSession() {
        return this;
    }

    @Override
    public ViewSessionTempTheme getTempThemeSession() {
        return this;
    }

    @Override
    public Choix_Special getChoixSpecial() {
        return getEnum("special", Choix_Special.class);
    }

    @Override
    public Photo_Action getPhotoAction() {
        return getEnum("action", Photo_Action.class);
    }

    @Override
    public Photo_Special getPhotoSpecial() {
        return getEnum("special", Photo_Special.class);
    }

    @Override
    public ViewSessionPhotoEdit getSessionPhotoEdit() {
        return this;
    }

    @Override
    public ViewSessionPhotoDisplay getSessionPhotoDisplay() {
        return this;
    }

    @Override
    public ViewSessionPhotoSubmit getSessionPhotoSubmit() {
        return this;
    }

    @Override
    public ViewSessionPhotoFastEdit getSessionPhotoFastEdit() {
        return this;
    }

    @Override
    public ViewSessionPhotoSimple getSessionPhotoSimple() {
        return this;
    }

    @Override
    public Tag_Special getTagSpecial() {
        return getEnum("special", Tag_Special.class);
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
        return this;
    }

    @Override
    public ViewSessionTagDisplay getSessionTagDisplay() {
        return this;
    }

    @Override
    public Carnet_Special getCarnetSpecial() {
        return getEnum("special", Carnet_Special.class);
    }

    @Override
    public Carnet_Action getCarnetAction() {
        return getEnum("action", Carnet_Action.class);
    }

    @Override
    public ViewSessionCarnetDisplay getSessionCarnetDisplay() {
        return this;
    }

    @Override
    public ViewSessionCarnetEdit getSessionCarnetEdit() {
        return this;
    }

    @Override
    public ViewSessionCarnetSubmit getSessionCarnetSubmit() {
        return this;
    }

    @Override
    public ViewSessionCarnetSimple getSessionCarnetSimple() {
        return this;
    }

    @Override
    public String getRawSpecial() {
        return getString("special");
    }

    @Override
    public ViewSessionAlbum getSessionAlbum() {
        return this;
    }

    @Override
    public ViewSessionChoix getSessionChoix() {
        return this;
    }

    @Override
    public ViewSessionPhoto getSessionPhoto() {
        return this;
    }

    @Override
    public ViewSessionAnAlbum getSessionAnAlbum() {
        return this;
    }

    @Override
    public ViewSessionLogin getSessionLogin() {
        return this;
    }

    @Override
    public ViewSessionImages getSessionImage() {
        return this;
    }

    @Override
    public ViewSessionTheme getSessionTheme() {
        return this;
    }

    @Override
    public ViewSessionDatabase getSessionDatabase() {
        return this;
    }

    @Override
    public SessionConfig getLocalSessionConfig() {
        return this;
    }

    @Override
    public ViewSessionTag getSessionTag() {
        return this;
    }

    @Override
    public ViewSessionCarnet getSessionCarnet() {
        return this;
    }

    @Override
    public Integer getNewPhotoAlbumSize() {
        return getInteger("photoAlbumSize");
    }

    @Override
    public Database_Action getDatabaseAction() {
        return getEnum("action", Database_Action.class);
    }
    
    @Override
    public ViewSessionImpl getSessionConfig() {
        return this;
    }

    @Override
    public Config_Special getConfigSpecial() {
        return getEnum("special", Config_Special.class);
    }

    @Override
    public Config_Action getConfigAction() {
        return getEnum("action", Config_Action.class);
    }

    @Override
    public boolean getWantMassedit() {
        return getEnum("action", Photo_Action.class) == Photo_Action.MASSEDIT;
    }

    public static class ViewSessionLoginImpl implements ViewSessionSession {
        private HttpSession session ;
        public ViewSessionLoginImpl (HttpSession session) {
            this.session = session ;
        }
        @Override
        public void setTempDir(File temp) {
            setSessionObject("tempDir", temp, session);
        }
        @Override
        public File getTempDir() {
            return ViewSessionImpl.getSessionObject("tempDir", File.class, session, null) ;
        }

        @Override
        public Configuration getConfiguration() {
            return ConfigurationXML.getConf() ;
        }
    }
}
