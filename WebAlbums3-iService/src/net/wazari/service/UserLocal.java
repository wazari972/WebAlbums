/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.service.exchange.ViewSessionLogin;

/**
 *
 * @author kevin
 */
@Local
public interface UserLocal {
    String MANAGER_ROLE = UtilisateurFacadeLocal.MANAGER_ROLE ;
    String VIEWER_ROLE  = UtilisateurFacadeLocal.VIEWER_ROLE  ;

    String USER_ADMIN   = "Admin"   ;
    String USER_FAMILLE = "Famille" ;
    String USER_AMIS    = "Amis"    ;
    String USER_PUBLIC  = "Autres"  ;

    @PermitAll
    boolean logon(ViewSessionLogin vSession, HttpServletRequest request) ;
    @PermitAll
    void cleanUpSession(ViewSessionLogin vSession) ;
}
