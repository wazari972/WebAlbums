/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.MaintFacadeLocal;
import org.hibernate.JDBCException;
import org.hibernate.ejb.EntityManagerImpl;
import org.hibernate.jmx.StatisticsService;
import org.hibernate.stat.QueryStatistics;
import org.hibernate.stat.Statistics;

/**
 *
 * @author kevinpouget
 */
@Stateless
public class MaintDAOBean implements MaintFacadeLocal {

    private static final Logger log = Logger.getLogger(MaintDAOBean.class.getName());

    private static interface Work {

        void execute(Connection connection) throws JDBCException;
    }
    @PersistenceContext(unitName = WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;
    @EJB
    ImportExporter xml;

    @Override
    public void treatImportXML(final String path) {
        if (WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_MySQL) {
            return;
        }
        xml.importXml(path);
    }

    @Override
    public void treatExportXML(String path) {
        xml.exportXml(path);
    }

    @Override
    public void treatTruncateDB() {
        if (WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_MySQL) {
            return;
        }
        xml.truncateDb();
    }

    @Override
    public void treatFullImport(String path) {
        if (WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_MySQL) {
            return;
        }
        treatTruncateDB();
        treatImportXML(path);
    }

    @Override
    public void treatUpdate() {
        try {
            log.info("registering Hibernate statistics MBean");

            ObjectName hibernateMBeanName = new ObjectName("Hibernate:type=statistics,application=WebAlbums");

            StatisticsService mBean = new StatisticsService();
            mBean.setStatisticsEnabled(true);
            mBean.setSessionFactory(((EntityManagerImpl) em.getDelegate()).getSession().getSessionFactory());
            try {
                ManagementFactory.getPlatformMBeanServer().unregisterMBean(hibernateMBeanName);
                log.warning("HibernateJMX was correctly undeployed");
            } catch (InstanceNotFoundException ex) {
                log.warning("HibernateJMX was not deployed");
            }
            ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, hibernateMBeanName);
        } catch (InstanceAlreadyExistsException ex) {
            Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MBeanRegistrationException ex) {
            Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotCompliantMBeanException ex) {
            Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedObjectNameException ex) {
            Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(MaintDAOBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void treatDumpStats() {
        Statistics stats = ((EntityManagerImpl) em.getDelegate()).getSession().getSessionFactory().getStatistics();

        stats.logSummary();
        for (String query : stats.getQueries()) {
            QueryStatistics qStats = stats.getQueryStatistics(query);
            log.info(query);
            log.log(Level.INFO, "\tgetExecutionCount {0}", qStats.getExecutionCount());
            log.log(Level.INFO, "\tgetExecutionAvgTime {0}", qStats.getExecutionAvgTime());
            log.log(Level.INFO, "\tgetExecutionMaxTime {0}", qStats.getExecutionMaxTime());
            log.log(Level.INFO, "\tgetExecutionRowCount {0}", qStats.getExecutionRowCount());
        }
    }
}
