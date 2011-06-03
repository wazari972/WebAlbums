/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.wazari.dao.AlbumFacadeLocal;
import net.wazari.dao.DatabaseFacadeLocal;
import net.wazari.dao.DatabaseFacadeLocal.DatabaseFacadeLocalException;
import net.wazari.dao.PhotoFacadeLocal;
import net.wazari.dao.ThemeFacadeLocal;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Theme;
import net.wazari.service.DatabaseLocal;
import net.wazari.service.entity.util.PhotoUtil;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionDatabase;
import net.wazari.service.exchange.xml.database.XmlDatabaseCheck;
import net.wazari.service.exchange.xml.database.XmlDatabaseDefault;
import net.wazari.service.exchange.xml.database.XmlDatabaseExport;
import net.wazari.service.exchange.xml.database.XmlDatabaseImport;
import net.wazari.service.exchange.xml.database.XmlDatabaseStats;
import net.wazari.service.exchange.xml.database.XmlDatabaseTrunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@Stateless
public class DatabaseBean implements DatabaseLocal {
    private static String getPath(Configuration conf) {
        return conf.getBackupPath();
    }
    private static final Logger log = LoggerFactory.getLogger(DatabaseBean.class.toString());
    @EJB ThemeFacadeLocal themeDAO;
    @EJB AlbumFacadeLocal albumDAO;
    @EJB PhotoFacadeLocal photoDAO;
    @EJB DatabaseFacadeLocal databaseDAO;
    @EJB private PhotoUtil photoUtil ;
    
    public XmlDatabaseImport treatIMPORT(ViewSession vSession) {
        XmlDatabaseImport output = new XmlDatabaseImport() ;
        try {
            databaseDAO.treatImportXML(vSession.getConfiguration().wantsProtectDB(), getPath(vSession.getConfiguration()));
            output.message = "Import OK";
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            output.exception = e.getMessage();
        }
        return output;
    }

    public XmlDatabaseExport treatEXPORT(ViewSession vSession) {
        XmlDatabaseExport output = new XmlDatabaseExport() ;
        try {
            databaseDAO.treatExportXML(getPath(vSession.getConfiguration()));
            output.message = "Export OK";
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            output.exception = e.getMessage();
        }
        return output;
    }

    public XmlDatabaseCheck treatCHECK(ViewSession vSession) {
        XmlDatabaseCheck output = new XmlDatabaseCheck() ;
        try {
            Theme enrTheme = vSession.getTheme();
            if (enrTheme == null)
                throw new DatabaseFacadeLocalException("Theme not set");
            if (vSession.getConfiguration().isPathURL()) {
                throw new DatabaseFacadeLocalException("URL path checking not implemented yet");
            }
            List<Theme> themes = new LinkedList<Theme>();
            if (vSession.isRootSession())
                //Hibernate exception if direct
                themes.add(themeDAO.find(enrTheme.getId()));
            else
                themes.addAll(themeDAO.findAll());
            
            int count = 0;
            for (Theme curEnrTheme : themeDAO.findAll()) {
                
                for (Album enrAlbum : curEnrTheme.getAlbumList()) {
                    log.warn("checking: {}", enrAlbum.getNom());
                    for (Photo enrPhoto : enrAlbum.getPhotoList()) {

                        for (String filepath : new String[]{photoUtil.getImagePath(vSession, enrPhoto), photoUtil.getMiniPath(vSession, enrPhoto)}) 
                        {
                            count++;
                            File f = new File(filepath);
                            if (!f.exists())
                                output.files.add(filepath+": missing");
                            else if (!f.canRead())
                                output.files.add(filepath+": not readable");
                        }
                    }
                }
            }
            output.message = "Check OK. "+count+" photos checked, "+output.files.size() + " missing";
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            output.exception = e.getMessage();
        }
        return output;
    }

    public XmlDatabaseTrunk treatTRUNK(ViewSession vSession) {
        XmlDatabaseTrunk output = new XmlDatabaseTrunk() ;
        try {
            databaseDAO.treatTruncateDB(vSession.getConfiguration().wantsProtectDB());
            output.message = "Trunk OK";
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            output.exception = e.getMessage();
        }
        return output;
    }

    public XmlDatabaseStats treatSTATS(ViewSessionDatabase vSession) {
        XmlDatabaseStats output = new XmlDatabaseStats() ;
        Theme enrTheme = vSession.getTheme();
        if (enrTheme == null) {
            output.exception = "theme not set";
            return output;
        }
        List<Theme> themes = new LinkedList<Theme>();
        if (vSession.isRootSession())
            //Hibernate exception if direct
            themes.add(themeDAO.find(enrTheme.getId()));
        else
            themes.addAll(themeDAO.findAll());

        int count = 0;
        for (Theme curEnrTheme : themeDAO.findAll()) {
            for (Album enrAlbum : curEnrTheme.getAlbumList()) {
                
            }
        }
        return output;
    }
    
    public XmlDatabaseDefault treatDEFAULT(ViewSessionDatabase vSession) {
        XmlDatabaseDefault output = new XmlDatabaseDefault() ;
        
        return output;
    }
    
}
