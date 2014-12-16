/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.DatabaseFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE})
public class DatabaseFacade implements DatabaseFacadeLocal {
    private static final Logger log = LoggerFactory.getLogger(DatabaseFacade.class.getName());

    @EJB
    ImportExporter xml;
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void treatImportXML(boolean protect, String path) throws DatabaseFacadeLocalException {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT.equals(WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod)) {
            throw new DatabaseFacadeLocalException("Protected");
        }
        log.info("Import from {}", path);
        xml.importXml(path);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void treatExportXML(String path) throws DatabaseFacadeLocalException {
        xml.exportXml(path);
    }
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void treatTruncateDB(boolean protect) throws DatabaseFacadeLocalException {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT.equals(WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod)) {
            throw new DatabaseFacadeLocalException("Protected");
        }
        xml.truncateDb();
    }    
}
