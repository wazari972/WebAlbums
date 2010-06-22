/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionSession;

/**
 *
 * @author kevin
 */
@Local
public interface SessionManagerLocal {

    @PermitAll
    void sessionCreated(ViewSessionSession vSession);

    @PermitAll
    void sessionDestroyed(ViewSession vSession);

}
