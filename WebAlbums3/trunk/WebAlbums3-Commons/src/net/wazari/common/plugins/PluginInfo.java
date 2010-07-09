/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.plugins;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
    
    @XmlElement List<Plugin> Importer = new LinkedList<Plugin>() ;
    @XmlElement Plugin System = null ;
    @XmlElement List<Plugin> NotUsedSystems = new LinkedList<Plugin>() ;

    public void addImporter(Importer importer) {
        Plugin plug = new Plugin() ;
        plug.name = importer.getName() ;
        plug.version = importer.getVersion() ;
        plug.description = importer.getDescription() ;
        plug.capabilities = Arrays.asList(importer.supports()) ;
        plug.priority = importer.getPriority();
        plug.supportedFiles = importer.getSupportedFilesDesc() ;
        plug.sanityCheck = importer.sanityCheck(ProcessCallback.getProcessCallBack());

        Importer.add(plug);
    }

    public void addNotUsedSystem(System syst) {
        Plugin plug = new Plugin() ;
        plug.name = syst.getName() ;
        plug.version = syst.getVersion() ;
        plug.sanityCheck = syst.sanityCheck(ProcessCallback.getProcessCallBack());

        NotUsedSystems.add(plug);
    }

    public void setUsedSystem(System syst) {
        log.warning("------") ; 
        System = new Plugin() ;
        System.name = syst.getName() ;
        System.version = syst.getVersion() ;
        System.sanityCheck = syst.sanityCheck(ProcessCallback.getProcessCallBack());
    }

    private static class Plugin {
        @XmlElement String name ;
        @XmlElement String version ;
        @XmlElement String description ;
        @XmlElement List<Importer.Capability> capabilities ;
        @XmlElement String supportedFiles ;
        @XmlElement Importer.SanityStatus sanityCheck ;
        @XmlElement int priority;
    }
}
