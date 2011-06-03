/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.servlet.exchange.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.common.plugins.XmlPluginInfo;
import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.database.XmlDatabaseCheck;
import net.wazari.service.exchange.xml.database.XmlDatabaseDefault;
import net.wazari.service.exchange.xml.database.XmlDatabaseExport;
import net.wazari.service.exchange.xml.database.XmlDatabaseImport;
import net.wazari.service.exchange.xml.database.XmlDatabaseStats;
import net.wazari.service.exchange.xml.database.XmlDatabaseTrunk;

/**
 *
 * @author kevin
  */
@XmlRootElement
public class XmlDatabase extends XmlInfoException {
    public XmlDatabaseCheck check;
    @XmlElement(name = "import")
    public XmlDatabaseImport import_;
    public XmlDatabaseExport export;
    public XmlDatabaseTrunk trunk;
    @XmlElement(name = "default")
    public XmlDatabaseDefault default_;
    public XmlDatabaseStats stats;
    public XmlPluginInfo plugins;
    public XmlCreateDir create_dir ;
    
    public static class XmlCreateDir {
        public List<String> dirs;
        public XmlCreateDir(){}
        public XmlCreateDir(List<String> dirs) {
            this.dirs = dirs;
        }
    }
}
