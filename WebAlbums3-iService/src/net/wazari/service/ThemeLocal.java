/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.ViewSessionTheme;
import net.wazari.service.exchange.xml.XmlThemeList;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE})
public interface ThemeLocal {
    enum Sort {NOPE, REVERSE, ALBUM_AGE}
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlThemeList getThemeList(ViewSessionTheme vSession, Sort order) ;
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlThemeList getThemeListSimple(ViewSession vSession) ;
}
