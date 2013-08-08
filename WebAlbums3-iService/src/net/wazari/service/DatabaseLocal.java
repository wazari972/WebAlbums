/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service;

import net.wazari.service.exchange.ViewSessionDatabase;
import net.wazari.service.exchange.xml.database.*;

/**
 *
 * @author kevin
 */
public interface DatabaseLocal {
    XmlDatabaseImport treatIMPORT(ViewSessionDatabase vSession);
    XmlDatabaseExport treatEXPORT(ViewSessionDatabase vSession);
    XmlDatabaseTrunk treatTRUNK(ViewSessionDatabase vSession);
    XmlDatabaseCheck treatCHECK(ViewSessionDatabase vSession);
    XmlDatabaseDefault treatDEFAULT(ViewSessionDatabase vSession);
    XmlDatabaseStats treatSTATS(ViewSessionDatabase vSession);
    void treatUPDATE(ViewSessionDatabase vSession);
    void treatUPDATE_DAO(ViewSessionDatabase vSession);
}
