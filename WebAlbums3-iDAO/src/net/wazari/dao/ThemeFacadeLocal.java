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
public interface ThemeFacadeLocal {
    int    THEME_ROOT_ID   = 1 ;
    String THEME_ROOT_NAME = "Root" ;

    void remove(Theme theme, boolean protect);

    Theme loadByName(String themeName) ;

    List<Theme> findAll() ;

    Theme find(Integer themeId);

    Theme newTheme(int i, String string);

    Theme newTheme(String string);

    void setPicture(Theme enrTheme, Photo pict);

    void setBackground(Theme enrTheme, Photo pict);

    void preconfigureDatabase();
    
    void edit(Theme enrTheme);
}
