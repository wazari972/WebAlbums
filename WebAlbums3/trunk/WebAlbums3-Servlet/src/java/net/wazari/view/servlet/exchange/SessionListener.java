/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.EJBException;
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
    private static final Logger log = LoggerFactory.getLogger(SessionListener.class.getName());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.warn("Session created") ;
        sessionService.sessionCreated(new ViewSessionLoginImpl(se.getSession()));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.warn("Session Destroyed") ;
        try {
            sessionService.sessionDestroyed(new ViewSessionLoginImpl(se.getSession()));
        } catch (EJBException e) {
            log.warn( "Too late: {}", e.getMessage()) ;
        }
    }
}
