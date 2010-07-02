/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Theme;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.ADMIN_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface ThemeFacadeLocal {
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void remove(Theme theme);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Theme loadByName(String themeName) ;

    @PermitAll
    List<Theme> findAll() ;

    @PermitAll
    Theme find(Integer themeId);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Theme newTheme(int i, String string);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    Theme newTheme(String string);
}
