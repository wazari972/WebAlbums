/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.MaintFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.jpa.entity.JPAAlbum;
import net.wazari.dao.jpa.entity.JPAAlbum_;
import net.wazari.dao.jpa.entity.JPATheme_;
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
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<JPAAlbum> cq = cb.createQuery(JPAAlbum.class) ;
        Root<JPAAlbum> albm = cq.from(JPAAlbum.class);

        log.warn("_+_"+JPAAlbum_.id+"_+_") ;

        cq.where(cb.equal(albm.get(JPAAlbum_.theme).get(JPATheme_.id), 5)) ;
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
