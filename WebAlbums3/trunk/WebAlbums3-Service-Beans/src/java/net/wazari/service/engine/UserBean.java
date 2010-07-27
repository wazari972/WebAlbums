package net.wazari.service.engine;

import java.security.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UserBean.class.getCanonicalName());
    private static final long serialVersionUID = 1L;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;

    @Override
    public boolean logon(ViewSessionLogin vSession, HttpServletRequest request) {
        Integer themeId = vSession.getThemeId();
        Principal pr = vSession.getUserPrincipal();
        log.info( "Login with theme={0}, principal={1}", new Object[]{themeId, pr});

        //user must be authenticated
        if (pr == null) {
            log.warn("User not authenticated");
            cleanUpSession(vSession);
            return false;
        }

        //look up the Theme entity
        if (themeId == null) {
            log.warn("No themeId provided");
            return false;
        }

        Theme enrTheme = themeDAO.find(themeId);
        if (enrTheme == null) {
            log.warn( "No such theme in the database: {0}", themeId);
            log.warn( "No such theme in the database: {0}", themeDAO.findAll());
            cleanUpSession(vSession);
            return false;
        }

        //check Root session special ID
        boolean isRootSession = enrTheme.getId().equals(ThemeFacadeLocal.THEME_ROOT_ID);
        boolean asThemeManager = false;

        String userName = null;
        if (request.isUserInRole(USER_ADMIN)) {
            userName = USER_ADMIN;
        } else if (request.isUserInRole(USER_FAMILLE)) {
            userName = USER_FAMILLE;
        } else if (request.isUserInRole(USER_AMIS)) {
            userName = USER_AMIS;
        } else if (request.isUserInRole(USER_AUTRES)) {
            userName = USER_AUTRES;
        }

        log.trace( "UserPrincipal :{0}", pr.getName());
        log.trace( "Role admin    :{0}", request.isUserInRole(UserLocal.MANAGER_ROLE));
        log.trace( "Role view     :{0}", request.isUserInRole(UserLocal.VIEWER_ROLE));
        log.trace( "DB User       :{0}", userName);

        Utilisateur enrUtil = userDAO.loadByName(userName);
        log.info( "database lookup returned: {0}", enrUtil);
        if (enrUtil == null) {
            log.warn( "No such user in the database: {0}", userName);
            log.warn( "No such user in the database: {0}", userDAO.findAll());
            cleanUpSession(vSession);
            return false;
        }

        //no manager if not in admin group
        if (request.isUserInRole(UserLocal.MANAGER_ROLE) && !vSession.getConfiguration().isReadOnly()) {
            asThemeManager = true;
        }

        log.info( "saveUser ({0}-{1})", new Object[]{enrUtil.getNom(), enrUtil.getId()});
        vSession.setUser(enrUtil);

        log.info( "saveProperties (manager={0}, editionMode={1}, rootSession={2})",
                new Object[]{asThemeManager, ViewSession.EditMode.EDITION, isRootSession});
        vSession.setSessionManager(asThemeManager);
        vSession.setEditionMode(ViewSession.EditMode.EDITION);
        vSession.setRootSession(isRootSession);

        log.info( "saveTheme ({0})", enrTheme);
        vSession.setTheme(enrTheme);

        return true;
    }

    @Override
    public void cleanUpSession(ViewSessionLogin vSession) {
        vSession.setUser(null);
        vSession.setSessionManager(null);
        vSession.setEditionMode(null);
        vSession.setRootSession(null);
        vSession.setTheme(null);
    }
}
