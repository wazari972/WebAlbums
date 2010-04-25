package net.wazari.service.engine;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;

import net.wazari.service.UserLocal;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Action;

import net.wazari.util.XmlBuilder;

@Stateless
public class UserBean implements UserLocal {
    private static Logger log = Logger.getLogger(UserBean.class.getCanonicalName()) ;
    private static final long serialVersionUID = 1L;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;

    public XmlBuilder treatUSR(ViewSession vSession) {
        XmlBuilder output = new XmlBuilder("userLogin");

        Action action = vSession.getAction();
        log.info("Action: "+action) ;
        boolean valid = false;
        if (Action.LOGIN == action) {
            clearSession(vSession) ;
            
            String userName = vSession.getUserName();
            boolean asThemeManager = false;
            log.info("userName: "+userName) ;
            if (userName == null) {
                output.add("denied");
                output.add("login");
                return output.validate();
            }
            //allow Theme+User to be logged as theme manager with given user view
            int indexOf = userName.indexOf('+');
            if ((indexOf == -1 && userName.equals(vSession.getThemeName())) ||
                    (indexOf != -1 && userName.substring(0, indexOf).equals(vSession.getThemeName()))) {
                asThemeManager = true;
                if (indexOf != -1) {
                    userName = userName.substring(indexOf + 1);
                }
            }

            Utilisateur enrUtil = null;
            
            enrUtil = userDAO.loadByName(userName);
            log.info("database lookup returned: "+enrUtil) ;
            if (enrUtil == null) asThemeManager = true ;
            
            String pass = vSession.getUserPass();
            if (saveSession(vSession, enrUtil, pass, asThemeManager)) {
                output.add("valid");
                valid = true;
            } else {
                output.add("denied");
                output.add("login");
            }
            log.info("authentication: "+valid);
        } else {
            output.add("login");
        }


        if (valid && vSession.getConfiguration().lightenDb()) {
//            Maint.keepOnlyTheme(output, themeID);
        }

        return output.validate();
    }
    
    private boolean saveSession(ViewSession vSession,
            Utilisateur enrUser,
            String passwd,
            boolean asThemeManager) {
        Integer userId;
        String goodPasswd;
        String userName;
        Integer themeId = vSession.getThemeId();
        String themeName ;
        boolean rootSession = false ;

        if (themeId == null) {
            return false;
        }
        
        Theme enrTheme = themeDAO.find(themeId);

        if (enrTheme == null) {
            return false;
        }
        themeName = enrTheme.getNom() ;
        rootSession = enrTheme.getId().equals(-1) ;
        
        if (asThemeManager) {
            goodPasswd = enrTheme.getPassword();

            userName = "Administrateur";
            if (enrUser != null) {
                userId = enrUser.getId();
                userName += "+" + enrUser.getNom();
            } else {
                userId = WebPageBean.USER_CHEAT;
            }
        } else {
            if (enrUser == null) {
                return false;
            }
            userName = enrUser.getNom();
            userId = enrUser.getId();
            goodPasswd = null;
        }

        if (goodPasswd != null && !goodPasswd.equals(passwd)) {
            return false;
        }

        log.info("saveUser (" + userName + "-" + userId + ")");
        vSession.setUserName(userName);
        vSession.setUserId(userId);
        log.info("saveProperties (manager=" + asThemeManager  +
                ", editionMode=" + ViewSession.EditMode.EDITION + "" +
                ", rootSession="+rootSession+")");
        vSession.setSessionManager(asThemeManager) ;
        vSession.setEditionMode(ViewSession.EditMode.EDITION) ;
        vSession.setRootSession(rootSession);
        log.info("saveTheme (" + themeName  + "-" + themeId + ")");
        vSession.setThemeId(themeId);
        vSession.setThemeName(themeName);
        return true;
    }
    private void clearSession(ViewSession vSession) {
        vSession.setUserName(null);
        vSession.setUserId(null);
        vSession.setSessionManager(null) ;
        vSession.setEditionMode(null) ;
        vSession.setRootSession(null);
        vSession.setThemeId(null);
        vSession.setThemeName(null);
    }
}
