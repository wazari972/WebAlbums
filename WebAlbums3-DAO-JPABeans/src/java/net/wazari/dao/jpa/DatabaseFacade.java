/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.DatabaseFacadeLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
public class DatabaseFacade implements DatabaseFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(DatabaseFacade.class.getName());

    @EJB
    ImportExporter xml;
    
    @Override
    public void treatImportXML(boolean protect, String path) throws DatabaseFacadeLocalException {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT.equals(WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod)) {
            throw new DatabaseFacadeLocalException();
        }
        xml.importXml(path);
    }

    @Override
    public void treatExportXML(String path) throws DatabaseFacadeLocalException {
        xml.exportXml(path);
    }
    
    @Override
    public void treatTruncateDB(boolean protect) {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT.equals(WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod)) {
            return;
        }
        xml.truncateDb();
    }

    @Override
    public void treatCheck(String path) throws DatabaseFacadeLocalException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
