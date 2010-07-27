package net.wazari.service.engine;

import net.wazari.service.SessionManagerLocal;
import java.io.File;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.service.exchange.ViewSessionSession;
import net.wazari.util.system.SystemTools;

@Stateless
public class SessionManagerBean implements SessionManagerLocal {

    private static final Logger log = LoggerFactory.getLogger(SessionManagerBean.class.toString());

    @EJB SystemTools sysTools ;
    /* Session Listener */
    @Override
    public void sessionCreated(ViewSessionSession vSession) {
        log.info( "Session created {0}", getUID());
        File temp = new File(vSession.getConfiguration().getTempPath() + vSession.getConfiguration().getSep() + getUID());
        log.info(temp.toString());
        if (!temp.mkdir()) {
            temp = null;
        } else {
            temp.deleteOnExit();
        }
        log.info( "temp dir created: {0}", temp);
        vSession.setTempDir(temp) ;
    }

    @Override
    public void sessionDestroyed(ViewSessionSession vSession) {
        File temp = vSession.getTempDir() ;
        if (temp != null) {
            log.info( "temp dir deleted: {0}", temp);
            temp.delete();
        }
        log.info( "Session destroyed {0}", getUID());
    }
    
    private String getUID() {
        UUID id = UUID.randomUUID() ;
        return id.toString() ;
    }
}
