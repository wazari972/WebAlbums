/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.ejb.Local;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.ViewSessionSession;

/**
 *
 * @author kevin
 */
@Local
public interface SessionManagerLocal {

    void sessionCreated(ViewSessionSession vSession);

    void sessionDestroyed(ViewSession vSession);

}
