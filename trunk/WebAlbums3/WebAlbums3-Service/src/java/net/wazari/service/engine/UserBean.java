package net.wazari.service.engine;

import java.security.Principal;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import javax.servlet.http.HttpServletRequest;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;

import net.wazari.service.UserLocal;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.ViewSessionLogin;


@Stateless
@DeclareRoles(UserLocal.ADMIN_ROLE)
public class UserBean implements UserLocal {
    private static Logger log = Logger.getLogger(UserBean.class.getCanonicalName()) ;
    private static final long serialVersionUID = 1L;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;

    @Override
    public boolean authenticate(ViewSessionLogin vSession, HttpServletRequest request) {
        Integer themeId = vSession.getThemeId() ;

        //look up the Theme entity
        if (themeId == null) {
            return false;
        }
        Theme enrTheme = themeDAO.find(themeId);
        if (enrTheme == null) {
            return false;
        }
        //check Root session special ID
        boolean isRootSession = enrTheme.getId().equals(-1) ;
        
        boolean asThemeManager = false ;

        Principal pr = vSession.getUserPrincipal() ;
        if (pr == null) return false ;
        
        String userName = pr.getName();
        log.fine("UserPrincipal : +"+userName) ;
        log.fine("Role admin    :" +request.isUserInRole(UserLocal.ADMIN_ROLE)) ;
        log.fine("Role view     :" +request.isUserInRole(UserLocal.VIEWER_ROLE)) ;

        int userId ;
        //allow +User to be logged as theme manager with given user view
        if (userName.indexOf('+') != -1) {
            asThemeManager = true;
            userName = userName.substring(1);
        }

        Utilisateur enrUtil = userDAO.loadByName(userName);
        log.info("database lookup returned: "+enrUtil) ;
        if (enrUtil == null) {
            asThemeManager = true ;
            userId = -1 ;
        } else {
            userId = enrUtil.getId() ;
        }

        //no manager if not in admin group
        if (!request.isUserInRole(UserLocal.ADMIN_ROLE)) {
            asThemeManager = false;
        }

        log.info("saveUser (" + userName + "-" + userId + ")");
        vSession.setUserName(userName);
        vSession.setUserId(userId);
        
        log.info("saveProperties (manager=" + asThemeManager  +
                ", editionMode=" + ViewSession.EditMode.EDITION + "" +
                ", rootSession="+isRootSession+")");
        vSession.setSessionManager(asThemeManager) ;
        vSession.setEditionMode(ViewSession.EditMode.EDITION) ;
        vSession.setRootSession(isRootSession);

        log.info("saveTheme (" + enrTheme  + ")");
        vSession.setTheme(enrTheme);

        return true ;
    }

    @Override
    public void cleanUpSession(ViewSessionLogin vSession) {
        vSession.setUserName(null);
        vSession.setUserId(null);
        vSession.setSessionManager(null) ;
        vSession.setEditionMode(null) ;
        vSession.setRootSession(null);
        vSession.setTheme(null);
        //vSession.setThemeName(null);
    }
}
