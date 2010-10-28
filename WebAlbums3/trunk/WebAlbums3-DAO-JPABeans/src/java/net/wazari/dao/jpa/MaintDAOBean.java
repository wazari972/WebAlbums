/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.MaintFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.entity.Photo;
import org.hibernate.ejb.EntityManagerImpl;
import org.hibernate.stat.Statistics;

/**
 *
 * @author kevinpouget
 */
@Stateless
public class MaintDAOBean implements MaintFacadeLocal {

    private static final Logger log = LoggerFactory.getLogger(MaintDAOBean.class.getName());
    @PersistenceContext(unitName = WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;
    @EJB
    ImportExporter xml;
    @EJB
    private PhotoFacadeLocal photoDAO;

    @Override
    public void treatImportXML(boolean protect, final String path) {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod) {
            return;
        }
        xml.importXml(path);
    }

    @Override
    public void treatExportXML(String path) {
        xml.exportXml(path);
    }

    @Override
    public void treatTruncateDB(boolean protect) {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod) {
            return;
        }
        xml.truncateDb();
    }

    @Override
    public void treatFullImport(boolean protect, String path) {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod) {
            return;
        }
        treatTruncateDB(protect);
        treatImportXML(protect, path);
    }

    @Override
    public void treatUpdate() {
        try {
            int count = 0 ;
            for (Photo enrPhoto : photoDAO.findAll()) {
                String theme = enrPhoto.getAlbum().getTheme().getNom() ;
                try {
                    //log.info("Path check {}", enrPhoto.getId());
                    String path = "file:///other/Web/data/miniatures/"+theme+"/"+enrPhoto.getPath()+".png" ;
                    new URL(path).openConnection().getInputStream().close() ;

                } catch (Exception e) {
                    log.info("Exception {}", e.getMessage());
                    count++ ;
                    for (Normalizer.Form form : Normalizer.Form.values()) {
                        String normalForm = null;
                        try {
                            normalForm = Normalizer.normalize(enrPhoto.getPath(), form);
                            //normalForm = StringUtil.escapeURL(normalForm);
                            new URL("file:///other/Web/data/miniatures/"+theme+"/"+normalForm+".png").openConnection().getInputStream();
                            //log.info("Path normalized with {}", form);
                            
                            File to = new File("/other/Web/data/miniatures/"+theme+"/"+enrPhoto.getPath().substring(0, enrPhoto.getPath().lastIndexOf("/"))) ;
                            File from = new File("/other/Web/data/miniatures/"+theme+"/"+normalForm.substring(0, normalForm.lastIndexOf("/"))) ;
                            log.info("from: {}", from);
                            log.info("to  : {}", to);
                            if (from.isDirectory()) {
                                to.mkdir();

                                for(File content : from.listFiles()) {
                                    String name = Normalizer.normalize(content.getName(), form);
                                    File target = new File(to, name) ;

                                    boolean cpy = content.renameTo(target);
                                    log.info("Copy: {} {}", cpy, content);
                                }
                                boolean del = from.delete();
                                log.info("delete {}", del);
                            }
                            break ;
                            //boolean ok = from.renameTo(to) ;
                            //log.info("renameTo: {} - {}", ok, normalForm);
                        } catch (IOException ex) {
                            log.warn("Normalisation {} failed: {}", form, normalForm);
                        }
                    }
                }
            }
            log.error("normalized: {}", count);
        } catch (Exception ex) {
            log.error("NullPointerException", ex);
        }
    }

    @Override
    public void treatDumpStats() {
        Statistics stats = ((EntityManagerImpl) em.getDelegate()).getSession().getSessionFactory().getStatistics();

        stats.logSummary();
        for (String query : stats.getQueries()) {
            //QueryStatistics qStats = stats.getQueryStatistics(query);
            log.info(query);
            //log.log(Level.INFO, "\tgetExecutionCount {}", qStats.getExecutionCount());
            //log.log(Level.INFO, "\tgetExecutionAvgTime {}", qStats.getExecutionAvgTime());
            //log.log(Level.INFO, "\tgetExecutionMaxTime {}", qStats.getExecutionMaxTime());
            //log.log(Level.INFO, "\tgetExecutionRowCount {}", qStats.getExecutionRowCount());
        }
    }
}
