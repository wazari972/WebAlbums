package net.wazari.service.engine;

import net.wazari.service.SessionManagerLocal;
import java.io.File;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.service.exchange.ViewSession;
import net.wazari.util.system.SystemToolsService;

@Stateless
public class SessionManagerBean implements SessionManagerLocal {

    private static final Logger log = Logger.getLogger(SessionManagerBean.class.toString());

    @EJB SystemToolsService sysTools ;
    /* Session Listener */
    public void sessionCreated(ViewSession vSession) {
        File temp = new File(vSession.getConfiguration().getTempDir() + "/" + getUID());
        log.info(temp.toString());
        if (!temp.mkdir()) {
            temp = null;
        } else {
            temp.deleteOnExit();
        }
        System.out.println("temp dir created: " + temp);
        vSession.setTempDir(temp) ;
    }

    public void sessionDestroyed(ViewSession vSession) {
        File temp = vSession.getTempDir() ;
        if (temp != null) {
            sysTools.remove(temp.toString());
        }
    }

    private int currentUID = 0 ;
    private String getUID() {
        return Integer.toString(currentUID++) ;
    }
}
