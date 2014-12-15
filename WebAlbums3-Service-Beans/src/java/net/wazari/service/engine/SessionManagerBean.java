package net.wazari.service.engine;

import java.io.File;
import java.util.UUID;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.service.SessionManagerLocal;
import net.wazari.service.exchange.ViewSessionSession;
import net.wazari.util.system.SystemTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class SessionManagerBean implements SessionManagerLocal {

    private static final Logger log = LoggerFactory.getLogger(SessionManagerBean.class.toString());

    @EJB SystemTools sysTools ;
    /* Session Listener */
    @Override
    @PermitAll
    public void sessionCreated(ViewSessionSession vSession) {
        log.info( "Session created {}", getUID());
        File temp = new File(vSession.getConfiguration().getTempPath() + vSession.getConfiguration().getSep() + getUID());
        log.info(temp.toString());
        if (!temp.mkdir()) {
            temp = null;
        } else {
            temp.deleteOnExit();
        }
        log.info( "temp dir created: {}", temp);
        vSession.setTempDir(temp) ;
    }

    @PermitAll
    @Override
    public void sessionDestroyed(ViewSessionSession vSession) {
        File temp = vSession.getTempDir() ;
        if (temp != null) {
            log.info( "temp dir deleted: {}", temp);
            temp.delete();
        }
        log.info( "Session destroyed {}", getUID());
    }
    
    private String getUID() {
        UUID id = UUID.randomUUID() ;
        return id.toString() ;
    }
}
