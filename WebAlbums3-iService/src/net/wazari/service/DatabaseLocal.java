/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service;

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
public interface DatabaseLocal {
    XmlDatabaseImport treatIMPORT(ViewSession vSession);
    XmlDatabaseExport treatEXPORT(ViewSession vSession);
    XmlDatabaseTrunk treatTRUNK(ViewSession vSession);
    XmlDatabaseCheck treatCHECK(ViewSession vSession);
    XmlDatabaseDefault treatDEFAULT(ViewSessionDatabase vSession);
}
