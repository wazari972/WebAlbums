package net.wazari.service.engine;

import net.wazari.service.SystemToolsLocal;
import net.wazari.service.SessionManagerLocal;
import java.io.File;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.ViewSessionSession;

@Stateless
public class SessionManagerBean implements SessionManagerLocal {

    private static final Logger log = Logger.getLogger(SessionManagerBean.class.toString());

    @EJB SystemToolsLocal sysTools ;
    /* Session Listener */
    @Override
    public void sessionCreated(ViewSessionSession vSession) {
        log.info("Session created " + getUID());
        File temp = new File(vSession.getConfiguration().getTempDir() + "/" + getUID());
        log.info(temp.toString());
        if (!temp.mkdir()) {
            temp = null;
        } else {
            temp.deleteOnExit();
        }
        log.info("temp dir created: " + temp);
        vSession.setTempDir(temp) ;
    }

    @Override
    public void sessionDestroyed(ViewSession vSession) {
        File temp = vSession.getTempDir() ;
        if (temp != null) {
            log.info("temp dir deleted: " + temp);
            temp.delete();
        }
        log.info("Session destroyed " + getUID());
    }

    private int currentUID = 0 ;
    private String getUID() {
        return Integer.toString(currentUID++) ;
    }
}
