package net.wazari.service.engine;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import javax.servlet.http.HttpServletRequest;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;

import net.wazari.service.UserLocal;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionLogin;


@Stateless
public class UserBean implements UserLocal {
    private static final Logger log = Logger.getLogger(UserBean.class.getCanonicalName()) ;
    private static final long serialVersionUID = 1L;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;

    @Override
    public boolean logon(ViewSessionLogin vSession, HttpServletRequest request) {

        Integer themeId = vSession.getThemeId() ;

        Principal pr = vSession.getUserPrincipal() ;

        log.log(Level.INFO, "Login with theme={0}, principal={1}", new Object[]{themeId, pr}) ;

        //user must be authenticated
        if (pr == null) {
            log.warning("User not authenticated") ;
            return false ;
        }

        //look up the Theme entity
        if (themeId == null) return false;
        Theme enrTheme = themeDAO.find(themeId);
        if (enrTheme == null) {
            log.warning("No such theme in the database: "+themeId) ;
            log.warning("No such theme in the database: "+themeDAO.findAll()) ;
            return false;
        }
        
        //check Root session special ID
        boolean isRootSession = enrTheme.getId().equals(-1) ;
        boolean asThemeManager = false ;

        String userName = pr.getName() ;
        log.log(Level.FINE, "UserPrincipal :{0}", userName) ;
        log.log(Level.FINE, "Role admin    :{0}", request.isUserInRole(UserLocal.ADMIN_ROLE)) ;
        log.log(Level.FINE, "Role view     :{0}", request.isUserInRole(UserLocal.VIEWER_ROLE)) ;

        //allow +User to be logged as theme manager with given user view
        if (userName.indexOf('+') != -1) {
            asThemeManager = true;
            userName = userName.substring(1);
        }

        Utilisateur enrUtil = userDAO.loadByName(userName);
        log.log(Level.INFO, "database lookup returned: {0}", enrUtil) ;
        if (enrUtil == null) {
            log.warning("No such user in the database: "+userName) ;
            log.warning("No such user in the database: "+userDAO.findAll()) ;
            return false ;
        } 

        //no manager if not in admin group
        if (request.isUserInRole(UserLocal.ADMIN_ROLE)) {
            asThemeManager = true;
        }

        log.log(Level.INFO, "saveUser ({0}-{1})", new Object[]{enrUtil.getNom(), enrUtil.getId()});
        vSession.setUser(enrUtil);

        log.log(Level.INFO,"saveProperties (manager={0}, editionMode={1}, rootSession={2})",
                new Object[]{asThemeManager, ViewSession.EditMode.EDITION, isRootSession});
        vSession.setSessionManager(asThemeManager) ;
        vSession.setEditionMode(ViewSession.EditMode.EDITION) ;
        vSession.setRootSession(isRootSession);

        log.log(Level.INFO, "saveTheme ({0})", enrTheme);
        vSession.setTheme(enrTheme);

        return true ;
    }

    @Override
    public void cleanUpSession(ViewSessionLogin vSession) {
        vSession.setUser(null);
        vSession.setSessionManager(null) ;
        vSession.setEditionMode(null) ;
        vSession.setRootSession(null);
        vSession.setTheme(null);
    }
}
