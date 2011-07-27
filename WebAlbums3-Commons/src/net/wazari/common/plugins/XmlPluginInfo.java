/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.plugins;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.common.plugins.Importer.SanityStatus;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement(name = "PluginInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlPluginInfo {
    private static final Logger log = LoggerFactory.getLogger(XmlPluginInfo.class.getName());
    
    @XmlElementWrapper(name="Importers") List<Plugin> WorkingImporters = new LinkedList<Plugin>() ;
    @XmlElementWrapper(name="Importers") List<Plugin> FailingImporters = new LinkedList<Plugin>() ;
    @XmlElement Plugin System = null ;
    @XmlElementWrapper(name="NotUsedSystems") List<Plugin> NotUsedSystems = new LinkedList<Plugin>() ;

    public void addImporter(Importer importer) {
        Plugin plug = new Plugin() ;
        plug.name = importer.getName() ;
        plug.version = importer.getVersion() ;
        plug.description = importer.getTargetSystem() ;
        plug.capability = Arrays.asList(importer.supports()) ;
        plug.priority = importer.getPriority();
        plug.supportedFiles = importer.getSupportedFilesDesc() ;
        plug.sanityCheck = importer.sanityCheck(ProcessCallbackImpl.getProcessCallBack());

        if (plug.sanityCheck == SanityStatus.PASS) {
            WorkingImporters.add(plug);
        } else {
            FailingImporters.add(plug);
        }
        
    }

    public void addNotUsedSystem(System syst) {
        Plugin plug = new Plugin() ;
        plug.name = syst.getName() ;
        plug.version = syst.getVersion() ;
        plug.sanityCheck = syst.sanityCheck(ProcessCallbackImpl.getProcessCallBack());

        NotUsedSystems.add(plug);
    }

    public void setUsedSystem(System syst) {
        if (syst == null) return ;
        log.warn("------") ; 
        System = new Plugin() ;
        System.name = syst.getName() ;
        System.version = syst.getVersion() ;
        System.sanityCheck = syst.sanityCheck(ProcessCallbackImpl.getProcessCallBack());
    }

    private static class Plugin {
        @XmlElement String name ;
        @XmlElement String version ;
        @XmlElement String description ;
        @XmlElementWrapper(name="Capabilities") List<Importer.Capability> capability ;
        @XmlElement String supportedFiles ;
        @XmlElement Importer.SanityStatus sanityCheck ;
        @XmlElement int priority;
    }
}
