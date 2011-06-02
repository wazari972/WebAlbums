/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.exchange.xml;

import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.database.XmlDatabaseCheck;
import net.wazari.service.exchange.xml.database.XmlDatabaseDefault;
import net.wazari.service.exchange.xml.database.XmlDatabaseExport;
import net.wazari.service.exchange.xml.database.XmlDatabaseImport;
import net.wazari.service.exchange.xml.database.XmlDatabaseTrunk;

/**
 *
 * @author kevin
  */
@XmlRootElement
public class XmlDatabase extends XmlInfoException {
    public XmlDatabaseCheck check;
    public XmlDatabaseImport import_;
    public XmlDatabaseExport export;
    public XmlDatabaseTrunk trunk;
    public XmlDatabaseDefault default_;
}
