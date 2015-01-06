/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service;

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

    boolean logon(ViewSessionLogin vSession, HttpServletRequest request) ;
    void cleanUpSession(ViewSessionLogin vSession) ;
}
