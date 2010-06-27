/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import net.wazari.service.SessionManagerLocal;
import net.wazari.view.servlet.exchange.ViewSessionImpl.ViewSessionLoginImpl;

/**
 * Web application lifecycle listener.
 * @author kevinpouget
 */
@WebListener
public class SessionListener implements HttpSessionListener {
    @EJB SessionManagerLocal sessionService ;
    private static final Logger log = Logger.getLogger(SessionListener.class.getName());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.warning("Session created") ;
        sessionService.sessionCreated(new ViewSessionLoginImpl(se.getSession()));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.warning("Session Destroyed") ;
        sessionService.sessionDestroyed(new ViewSessionLoginImpl(se.getSession()));
    }
}
