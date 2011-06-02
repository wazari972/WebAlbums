/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.engine;

import javax.ejb.EJB;
import net.wazari.dao.DatabaseFacadeLocal;
import net.wazari.service.DatabaseLocal;
import net.wazari.service.exchange.Configuration;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionDatabase;
import net.wazari.service.exchange.xml.database.XmlDatabaseCheck;
import net.wazari.service.exchange.xml.database.XmlDatabaseDefault;
import net.wazari.service.exchange.xml.database.XmlDatabaseExport;
import net.wazari.service.exchange.xml.database.XmlDatabaseImport;
import net.wazari.service.exchange.xml.database.XmlDatabaseTrunk;

/**
 *
 * @author kevin
 */

public class DatabaseBean implements DatabaseLocal {
    private static String getPath(Configuration conf) {
        return conf.getBackupPath();
    }
    
    @EJB DatabaseFacadeLocal databaseDAO;
    public XmlDatabaseImport treatIMPORT(ViewSession vSession) {
        try {
            databaseDAO.treatImportXML(vSession.getConfiguration().wantsProtectDB(), getPath(vSession.getConfiguration()));
            return new XmlDatabaseImport() ;
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            return null ;
        }
    }

    public XmlDatabaseExport treatEXPORT(ViewSession vSession) {
        try {
            databaseDAO.treatExportXML(getPath(vSession.getConfiguration()));
            return new XmlDatabaseExport() ;
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            return null ;
        }
    }

    public XmlDatabaseCheck treatCHECK(ViewSession vSession) {
        try {
            databaseDAO.treatCheck(getPath(vSession.getConfiguration()));
            return new XmlDatabaseCheck() ;
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            return null ;
        }
    }

    public XmlDatabaseTrunk treatTRUNK(ViewSession vSession) {
        try {
            databaseDAO.treatTruncateDB(vSession.getConfiguration().wantsProtectDB());
            return new XmlDatabaseTrunk() ;
        } catch (DatabaseFacadeLocal.DatabaseFacadeLocalException e) {
            return null ;
        }
    }

    public XmlDatabaseDefault treatDEFAULT(ViewSessionDatabase vSession) {
        return new XmlDatabaseDefault();
    }
    
}
