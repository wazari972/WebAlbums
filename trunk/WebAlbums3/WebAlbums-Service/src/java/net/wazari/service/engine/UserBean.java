package net.wazari.service.engine;

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

    private static final long serialVersionUID = 1L;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;

    public XmlBuilder treatUSR(ViewSession vSession) {
        XmlBuilder output = new XmlBuilder("userLogin");

        Action action = vSession.getAction();

        vSession.setUserId(null);

        boolean valid = false;

        if (Action.LOGIN == action) {
            String userName = vSession.getUserName();
            boolean asThemeManager = false;

            if (userName == null) {
                output.add("denied");
                output.add("login");
                return output.validate();
            }

            int indexOf = userName.indexOf('+');
            if ((indexOf == -1 && userName.equals(vSession.getThemeName())) ||
                    (indexOf != -1 && userName.substring(0, indexOf).equals(vSession.getThemeName()))) {
                asThemeManager = true;
                if (indexOf != -1) {
                    userName = userName.substring(indexOf + 1);
                }
            }

            Utilisateur enrUtil = null;
            //log.info("look for user " + userName + " as Admin");
            enrUtil = userDAO.loadByName(userName);

            String pass = vSession.getUserPass();
            if (saveUser(vSession, enrUtil, pass, asThemeManager)) {
                output.add("valid");
                valid = true;
            } else {
                output.add("denied");
                output.add("login");
            }
        } else {
            output.add("login");
        }


        if (valid && vSession.getConfiguration().lightenDb()) {
//            Maint.keepOnlyTheme(output, themeID);
        }

        return output.validate();
    }

    public boolean saveUser(ViewSession vSession,
            Utilisateur enrUser,
            String passwd,
            boolean asThemeManager) {
        String userID;
        String goodPasswd;
        String userName;
        Integer themeID = vSession.getThemeId();

        if (themeID == null) {
            return false;
        }

        if (asThemeManager) {
            Theme enrTheme = themeDAO.find(themeID);

            if (enrTheme == null) {
                return false;
            }
            goodPasswd = enrTheme.getPassword();

            userName = "Administrateur";
            if (enrUser != null) {
                userID = enrUser.getId().toString();
                userName += "+" + enrUser.getNom();
            } else {
                userID = WebPageBean.USER_CHEAT;
            }
        } else {
            if (enrUser == null) {
                return false;
            }
            userName = enrUser.getNom();
            userID = enrUser.getId().toString();
            goodPasswd = null;
        }

        if (goodPasswd != null && !goodPasswd.equals(passwd)) {
            Integer autoLogin = vSession.getConfiguration().autoLogin();
            if (!themeID.equals(autoLogin)) {
                return false;
            }
        }

        //WebPage.log.info("saveUser (" + userName + "-" + userID + ")");
        vSession.setUserName(userName);
        vSession.setEditionMode(ViewSession.EditMode.EDITION) ;
        vSession.setRootSession(asThemeManager);
        vSession.setUserId(userID);
        return true;
    }
}
