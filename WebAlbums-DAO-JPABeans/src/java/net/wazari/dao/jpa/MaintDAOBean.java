/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.text.Normalizer;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import net.wazari.dao.DatabaseFacadeLocal.DatabaseFacadeLocalException;
import net.wazari.dao.MaintFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@Stateless
@DeclareRoles({UtilisateurFacadeLocal.MANAGER_ROLE})
public class MaintDAOBean implements MaintFacadeLocal {

    private static final Logger log = LoggerFactory.getLogger(MaintDAOBean.class.getName());
    @PersistenceContext(unitName = WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;
    @EJB ImportExporter xml;
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void treatImportXML(boolean protect, final String path) throws DatabaseFacadeLocalException {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod) {
            return;
        }
        xml.importXml(path);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void treatExportXML(String path) throws DatabaseFacadeLocalException {
        xml.exportXml(path);
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void treatTruncateDB(boolean protect) {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod) {
            return;
        }
        xml.truncateDb();
    }

    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void treatFullImport(boolean protect, String path) throws DatabaseFacadeLocalException  {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod) {
            return;
        }
        treatTruncateDB(protect);
        treatImportXML(protect, path);
    }
    public static String sansAccents(String source) {
            return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
    }
    
    
    
    @Override
    @RolesAllowed(UtilisateurFacadeLocal.MANAGER_ROLE)
    public void treatUpdate() {
        
    }
}
