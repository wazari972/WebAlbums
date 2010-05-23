/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import net.wazari.service.exchange.ViewSession;

/**
 *
 * @author kevin
 */
@Local
public interface UserLocal {
    public final static String ADMIN_ROLE = "ADMIN" ;
    public final static String VIEWER_ROLE = "VIEWER" ;
    //XmlBuilder treatUSR(ViewSession vSession) throws WebAlbumsServiceException;

    boolean authenticate(ViewSession vSession, HttpServletRequest request) ;
    void cleanUpSession(ViewSession vSession) ;
}
