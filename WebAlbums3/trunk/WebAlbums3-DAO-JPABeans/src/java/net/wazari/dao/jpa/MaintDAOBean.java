/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.sql.Connection;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.MaintFacadeLocal;
import org.hibernate.JDBCException;

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

    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;

    @EJB ImportExporter xml ;

    @Override
    public void treatImportXML(final String path) {
        if (WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_MySQL) return ;
        xml.importXml(path);
    }
    
    @Override
    public void treatExportXML(String path) {
        xml.exportXml(path);
    }

    @Override
    public void treatTruncateDB() {
        if (WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_MySQL) return ;
        xml.truncateDb();
    }

    @Override
    public void treatFullImport(String path) {
        if (WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_MySQL) return ;
        treatTruncateDB();
        treatImportXML(path);
    }
}
