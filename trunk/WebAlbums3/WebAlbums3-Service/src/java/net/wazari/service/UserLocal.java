/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.service.exchange.ViewSession.ViewSessionLogin;

/**
 *
 * @author kevin
 */
@Local
public interface UserLocal {
    public final static String ADMIN_ROLE = UtilisateurFacadeLocal.ADMIN_ROLE ;
    public final static String VIEWER_ROLE = UtilisateurFacadeLocal.VIEWER_ROLE;
    //XmlBuilder treatUSR(ViewSession vSession) throws WebAlbumsServiceException;

    boolean authenticate(ViewSessionLogin vSession, HttpServletRequest request) ;
    void cleanUpSession(ViewSessionLogin vSession) ;
}
