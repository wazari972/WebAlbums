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
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Theme;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE, UtilisateurFacadeLocal.VIEWER_ROLE})
public interface ThemeFacadeLocal {
    int    THEME_ROOT_ID   = 1 ;
    String THEME_ROOT_NAME = "Root" ;

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void remove(Theme theme, boolean protect);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Theme loadByName(String themeName) ;

    @PermitAll
    List<Theme> findAll() ;

    @PermitAll
    Theme find(Integer themeId);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Theme newTheme(int i, String string);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    Theme newTheme(String string);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void setPicture(Theme enrTheme, Photo pict);

    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    void setBackground(Theme enrTheme, Photo pict);
}
