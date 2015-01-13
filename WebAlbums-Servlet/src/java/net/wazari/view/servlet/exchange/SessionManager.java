package net.wazari.view.servlet.exchange;

import java.io.File;
import java.util.UUID;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSessionSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class.toString());
    @EJB private Configuration configuration;
    
    public void sessionCreated(ViewSessionSession vSession) {
        log.info("Session created {}", getUID());
        File temp = new File(configuration.getTempPath() + configuration.getSep() + getUID());
        log.info(temp.toString());
        if (!temp.mkdir()) {
            temp = null;
        } else {
            temp.deleteOnExit();
        }
        log.info("temp dir created: {}", temp);
        vSession.setTempDir(temp) ;
    }

    public void sessionDestroyed(ViewSessionSession vSession) {
        File temp = vSession.getTempDir() ;
        if (temp != null) {
            log.info("temp dir deleted: {}", temp);
            temp.delete();
        }
        log.info("Session destroyed {}", getUID());
    }
    
    private String getUID() {
        UUID id = UUID.randomUUID() ;
        return id.toString() ;
    }
}
