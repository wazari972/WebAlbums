/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.JAXBException;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.TagPhotoFacadeLocal;
import net.wazari.dao.TagThemeFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.UtilisateurFacadeLocal;
import net.wazari.dao.jpa.entity.xml.WebAlbumsXML;
import net.wazari.common.util.XmlUtils ;
import net.wazari.dao.jpa.entity.JPATag;
/**
 *
 * @author kevinpouget
 */
@Stateless
public class XMLImportExport implements ImportExporter {
    private static final Logger log = Logger.getLogger(XMLImportExport.class.getName());

    @EJB
    private TagThemeFacadeLocal tagThemeDAO;
    @EJB
    private UtilisateurFacadeLocal userDAO;
    @EJB
    private TagPhotoFacadeLocal tagPhotoDAO;
    @EJB
    private TagFacadeLocal tagDAO;
    @EJB
    private ThemeFacadeLocal themeDAO;
    @EJB
    private AlbumFacadeLocal albumDAO;
    @EJB
    private PhotoFacadeLocal photoDAO ;
    
    @PersistenceContext(unitName=WebAlbumsDAOBean.PERSISTENCE_UNIT)
    private EntityManager em;
    
    public void exportXml(String path) {
        WebAlbumsXML web = new WebAlbumsXML(themeDAO.findAll(), userDAO.findAll(), albumDAO.findAll(),
                photoDAO.findAll(), tagDAO.findAll(), tagThemeDAO.findAll(), tagPhotoDAO.findAll()) ;
        try {
            log.info(XmlUtils.print(web, WebAlbumsXML.class));
        } catch (JAXBException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    public void importXml(String path) {
        try {
            WebAlbumsXML web = XmlUtils.reload(new File(path+"WebAlbum.xml"), WebAlbumsXML.class);

            for (Object enr : web.getThemes()) {
                em.merge(enr);
            }

            for (Object enr : web.getUtilisateurs()) {
                em.merge(enr);
            }

            for (Object enr : web.getAlbums()) {
                em.merge(enr);
            }

            for (Object enr : web.getPhotos()) {
                em.merge(enr);
            }

            for (JPATag enrTag : web.getTags()) {
                em.merge(enrTag);
                if (enrTag.getGeolocalisation() != null) {
                    em.merge(enrTag.getGeolocalisation()) ;
                 }
            }

            for (Object enr : web.getTagPhoto()) {
                em.merge(enr);
            }

            for (Object enr : web.getTagThemes()) {
                em.merge(enr);
            }
        } catch (JAXBException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }
    
    public void truncateDb() {

        for (Object enr : themeDAO.findAll()) {
            em.remove(enr);
        }

        for (Object enr : userDAO.findAll()) {
            em.remove(enr);
        }
    }
}
