/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.TagTheme;

/**
 *
 * @author kevin
 */
@Local
public interface TagThemeFacadeLocal {
    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void create(TagTheme tagTheme);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void edit(TagTheme tagTheme);

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    void remove(TagTheme tagTheme);

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    TagTheme loadByTagTheme(Integer tagID, Integer themeID) ;

    @RolesAllowed(UtilisateurFacadeLocal.ADMIN_ROLE)
    TagTheme newTagTheme();
}
