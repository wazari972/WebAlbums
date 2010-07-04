/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.plugins;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement(name = "PluginInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class PluginInfo {
    private static final Logger log = Logger.getLogger(PluginInfo.class.getName());
    
    @XmlElement List<ImporterPlugin> Plugins = new LinkedList<ImporterPlugin>() ;

    public void addPlugin(Importer importer) {
        ImporterPlugin plug = new ImporterPlugin() ;
        log.log(Level.INFO, "Set name:{0}", importer.getName()) ;
        plug.name = importer.getName() ;
        Plugins.add(plug);
    }

    private static class ImporterPlugin {
        @XmlElement String name ;
        @XmlElement String version ;
    }
}
