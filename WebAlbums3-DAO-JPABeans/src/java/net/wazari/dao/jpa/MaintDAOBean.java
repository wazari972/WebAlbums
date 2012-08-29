/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.jpa;

import java.text.Normalizer;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.wazari.dao.DatabaseFacadeLocal.DatabaseFacadeLocalException;
import net.wazari.dao.MaintFacadeLocal;
import net.wazari.dao.jpa.entity.JPAPhoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@Stateless
public class MaintDAOBean implements MaintFacadeLocal {

    private static final Logger log = LoggerFactory.getLogger(MaintDAOBean.class.getName());
    @PersistenceContext(unitName = WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;
    @EJB ImportExporter xml;
    
    @Override
    public void treatImportXML(boolean protect, final String path) throws DatabaseFacadeLocalException {
        if (protect || WebAlbumsDAOBean.PERSISTENCE_UNIT == WebAlbumsDAOBean.PERSISTENCE_UNIT_Prod) {
            return;
        }
        xml.importXml(path);
    }

    @Override
    public void treatExportXML(String path) throws DatabaseFacadeLocalException {
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
    public void treatUpdate() {
        
    }
}
