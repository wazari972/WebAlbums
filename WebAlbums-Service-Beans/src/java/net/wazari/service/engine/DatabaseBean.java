/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.DatabaseFacadeLocal;
import net.wazari.dao.DatabaseFacadeLocal.DatabaseFacadeLocalException;
import net.wazari.dao.MaintFacadeLocal;
import net.wazari.dao.TagFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.Theme;
import net.wazari.service.DatabaseLocal;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSessionDatabase;
import net.wazari.service.exchange.ViewSessionDatabase.Database_Action;
import net.wazari.service.exchange.xml.database.XmlDatabaseCheck;
import net.wazari.service.exchange.xml.database.XmlDatabaseDefault;
import net.wazari.service.exchange.xml.database.XmlDatabaseExport;
import net.wazari.service.exchange.xml.database.XmlDatabaseImport;
import net.wazari.service.exchange.xml.database.XmlDatabaseStats;
import net.wazari.service.exchange.xml.database.XmlDatabaseStats.XmlDatabaseStatsTheme;
import net.wazari.service.exchange.xml.database.XmlDatabaseTrunk;
import net.wazari.service.exchange.xml.tag.XmlTag;
import net.wazari.service.exchange.xml.tag.XmlTagCloud.XmlTagCloudEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
public class DatabaseBean implements DatabaseLocal {
    private static final Logger log = LoggerFactory.getLogger(DatabaseBean.class.toString());
    @EJB ThemeFacadeLocal themeDAO;
    @EJB TagFacadeLocal tagDAO;
    @EJB DatabaseFacadeLocal databaseDAO;
    @EJB private PhotoUtil photoUtil ;
    @EJB MaintFacadeLocal maintDAO;
    @EJB private Configuration configuration;
    
    @Override
    public XmlDatabaseImport treatIMPORT(ViewSessionDatabase vSession) {
        XmlDatabaseImport output = new XmlDatabaseImport() ;
        try {
            databaseDAO.treatImportXML(configuration.wantsProtectDB(), configuration.getBackupPath());
            output.message = "Import OK";
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            output.exception = e.getMessage();
            log.warn("Couldn't import ... {}", e);
        }
        return output;
    }

    @Override
    public XmlDatabaseExport treatEXPORT(ViewSessionDatabase vSession) {
        XmlDatabaseExport output = new XmlDatabaseExport() ;
        try {
            databaseDAO.treatExportXML(configuration.getBackupPath());
            output.message = "Export OK";
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            output.exception = e.getMessage();
            log.warn("Couldn't export ... {}", e);
        }
        return output;
    }

    @Override
    public XmlDatabaseCheck treatCHECK(ViewSessionDatabase vSession) {
        XmlDatabaseCheck output = new XmlDatabaseCheck() ;
        
        Database_Action action = vSession.getDatabaseAction() ;
        if (action == null) {
            return output;
        }
        
        try {
            Theme enrTheme = themeDAO.find(vSession.getVSession().getTheme().getId());
            if (enrTheme == null) {
                throw new DatabaseFacadeLocalException("Theme not set");
            }
            if (configuration.isPathURL()) {
                throw new DatabaseFacadeLocalException("URL path checking not implemented yet");
            }
            List<Theme> themes = new LinkedList<>();
            if (vSession.getVSession().isRootSession()) {
                themes.addAll(themeDAO.findAll());
            }
            else {
                themes.add(enrTheme);
            }
            
            int count = 0;
            for (Theme curEnrTheme : themes) {
                if (curEnrTheme.getId() == ThemeFacadeLocal.THEME_ROOT_ID) {
                    continue ;
                }
                List<String> images = null;
                List<String> mini = null;
                
                if (action == Database_Action.CHECK_FS) {
                    /* Save in images and mini the path to all the files of the directory.  */
                    images = new LinkedList<>();
                    mini = new LinkedList<>();
                    String sep = configuration.getSep() ;
                    List<String> current = images ;
                    String pictpath = configuration.getImagesPath(true) ;
                    
                    while (current != null) {    
                        File themeDir = new File(pictpath + sep + curEnrTheme.getNom());
                        for (File year : themeDir.listFiles()) {
                            for (File albums : year.listFiles()) {
                                for (File image : albums.listFiles()) {
                                    if (image.getName().endsWith(".gpx")) {
                                        continue ;
                                    }
                                    current.add(image.getAbsolutePath());
                                }
                            }
                        }
                        
                        if (current == images) {
                            current = mini; 
                            pictpath = configuration.getMiniPath(true) ;
                        } else {
                            current = null;
                        }
                    }
                }
                for (Album enrAlbum : curEnrTheme.getAlbumList()) {
                    log.warn("checking album: {}", enrAlbum.getNom());
                    for (Photo enrPhoto : enrAlbum.getPhotoList()) {
                        count++;
                        List<String> current = images ;
                        for (String filepath : new String[]{photoUtil.getImagePath(vSession.getVSession(), enrPhoto), 
                                                            photoUtil.getMiniPath(vSession.getVSession(), enrPhoto)}) {
                            if (enrPhoto.isGpx() && filepath.contains(configuration.getMiniPath(true))) {
                                continue;
                            }
                            if (action == Database_Action.CHECK_DB) {
                                File f = new File(filepath);
                                if (!f.exists()) {
                                    output.files.put(enrPhoto.getId().toString(), filepath + " missing");
                                } else if (!f.canRead()) {
                                    output.files.put(enrPhoto.getId().toString(), filepath + " not readable");
                                }
                            } else if (action == Database_Action.CHECK_FS) {
                                boolean removed = current.remove(filepath);
                                log.info("checking: {}", filepath);
                                log.info("found ^: {}", removed);
                                current = mini ;
                            }
                        }
                    }
                }
                if (action == Database_Action.CHECK_FS) {
                    for (List<String> lst : new List[]{images, mini}) {
                        for (String name : lst) {
                            output.files.put(name, "not found");
                        }
                    }
                }
            }
            
            output.message = "Check OK. "+count+" photos checked, "+output.files.size() + " problems";
            log.warn(output.message);
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            output.exception = e.getMessage();
            log.warn("Couldn't check ... {}", e);
        }
        return output;
    }

    @Override
    public XmlDatabaseTrunk treatTRUNK(ViewSessionDatabase vSession) {
        XmlDatabaseTrunk output = new XmlDatabaseTrunk() ;
        try {
            databaseDAO.treatTruncateDB(configuration.wantsProtectDB());
            output.message = "Trunk OK";
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            output.exception = e.getMessage();
            log.warn("Couldn't trunk ... {}", e);
        }
        return output;
    }

    @Override
    public XmlDatabaseStats treatSTATS(ViewSessionDatabase vSession) {
        XmlDatabaseStats output = new XmlDatabaseStats() ;
        Theme enrTheme = vSession.getVSession().getTheme();
        if (enrTheme == null) {
            output.exception = "theme not set";
            return output;
        }
        List<Theme> themes = new LinkedList<>();
        XmlDatabaseStatsTheme xmlRootTheme = null;
        if (!vSession.getVSession().isRootSession()) {
            //Hibernate exception if direct
            themes.add(themeDAO.find(enrTheme.getId()));
        } else {
            themes.addAll(themeDAO.findAll());
            xmlRootTheme = new XmlDatabaseStatsTheme("root");
        }
        for (Theme curEnrTheme : themes) {
            if (curEnrTheme.getId() == 1) {
                continue;
            }
            XmlDatabaseStatsTheme xmlTheme = new XmlDatabaseStatsTheme(curEnrTheme.getNom());
            output.theme.add(xmlTheme);
            
            for (Album enrAlbum : curEnrTheme.getAlbumList()) {
                int photosize = enrAlbum.getPhotoList().size();
                xmlTheme.albums++;
                xmlTheme.photos += photosize;
                if (xmlRootTheme != null) {
                    xmlRootTheme.albums++;
                    xmlRootTheme.photos += photosize;
                }
            }
            if (xmlRootTheme != null) {
                vSession.setRootSession(Boolean.FALSE);
                vSession.setTheme(curEnrTheme);
            }
            
            xmlTheme.tags = tagDAO.loadVisibleTags(vSession.getVSession(), false, true).size();
            if (xmlRootTheme != null) {
                vSession.setRootSession(Boolean.TRUE);
                vSession.setTheme(enrTheme);
            }
            
            if (xmlRootTheme == null || xmlRootTheme.tag == null) {
                Map<Tag, Long> map = tagDAO.queryIDNameCount(vSession.getVSession());
                List<XmlTagCloudEntry> lst = new ArrayList<>(map.size());
                for (Tag enrTag : map.keySet()) {
                    XmlTagCloudEntry xmlCloudEntry = new XmlTagCloudEntry();
                    xmlCloudEntry.tag = new XmlTag();
                    xmlCloudEntry.tag.name  = enrTag.getNom();
                    xmlCloudEntry.nb = map.get(enrTag);
                    lst.add(xmlCloudEntry);
                }
                if (xmlRootTheme == null) {
                    xmlTheme.tag = lst;
                } else {
                    xmlRootTheme.tag = lst;
                }
            }
        }
        
        if (xmlRootTheme != null)  {
            output.theme.add(xmlRootTheme);
            xmlRootTheme.tags = tagDAO.findAll().size();
        }
        return output;
    }
    
    @Override
    public XmlDatabaseDefault treatDEFAULT(ViewSessionDatabase vSession) {
        XmlDatabaseDefault output = new XmlDatabaseDefault() ;
        
        return output;
    }
    
    @Override
    public void treatUPDATE(ViewSessionDatabase vSession) {
        
    }
    
    @Override
    public void treatUPDATE_DAO(ViewSessionDatabase vSession) {
        maintDAO.treatUpdate();
    }
    
}
