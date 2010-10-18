/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.xml.XmlThemes;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE})
public interface ThemeLocal {
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlThemes getThemeList(ViewSession vSession) ;
}
