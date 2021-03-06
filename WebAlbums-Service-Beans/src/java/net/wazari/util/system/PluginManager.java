/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.util.system;

import java.io.File;
import java.util.*;
import javax.ejb.Singleton;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.System;
import net.wazari.common.util.ClassPathUtil;
import net.wazari.service.PluginManagerLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@Singleton
public class PluginManager implements PluginManagerLocal {

    private static final Logger log = LoggerFactory.getLogger(PluginManager.class.getCanonicalName());
    private final List<Importer> validWrappers = new LinkedList<>();
    private final List<Importer> invalidWrappers = new LinkedList<>();
    private System system = null;
    private final List<System> notUsedSystems = new LinkedList<>();

    @Override
    public List<Importer> getPluginList() {
        List<Importer> wrappers = new LinkedList<>(validWrappers);
        wrappers.addAll(invalidWrappers);
        return wrappers;
    }

    @Override
    public List<Importer> getWorkingPlugins() {
        return validWrappers;
    }

    @Override
    public List<System> getNotUsedSystemList() {
        return notUsedSystems;
    }

    @Override
    public System getUsedSystem() {
        return system;
    }

    @Override
    public void reloadPlugins(String path) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
        if (path != null) {
            log.info("Adding plugins from {}", path);
            cl = ClassPathUtil.addDirToClasspath(new File(path));
        }
        validWrappers.clear();
        invalidWrappers.clear();
        log.info("Loading services for \"{}\"", Importer.class.getCanonicalName());
        ServiceLoader<Importer> servicesImg = ServiceLoader.load(Importer.class, cl);
        Iterator<Importer> itImg = servicesImg.iterator();
        while (itImg.hasNext()) {
            try {
                Importer current = itImg.next();
                log.info("Adding \"{}\"", current.getClass().getCanonicalName());
                boolean pass ;
                try {
                    pass = current.sanityCheck(SystemTools.cb) == Importer.SanityStatus.PASS;
                } catch (UnsupportedOperationException e) {
                    pass = false;
                }
                
                if (pass) {
                    validWrappers.add(current);
                } else {
                    invalidWrappers.add(current);
                }
            } catch (ServiceConfigurationError e) {
                log.warn("Couldn''t load the service...", e);
            }
        }

        Collections.sort(validWrappers, new Comparator<Importer>() {

            @Override
            public int compare(Importer o1, Importer o2) {
                try {
                    return o2.getPriority() - o1.getPriority();
                } catch (UnsupportedOperationException e) {
                    return 0;
                }
            }
        });

        log.info("Loading services for \"{}\"", System.class.getCanonicalName());
        ServiceLoader<System> servicesSys = ServiceLoader.load(System.class, cl);
        this.system = null;
        Iterator<System> itSyst = servicesSys.iterator();
        while (itSyst.hasNext()) {
            try {
                System current = itSyst.next();
                log.info("Adding \"{}\"", current.getClass().getCanonicalName());
                if (system == null && current.sanityCheck(SystemTools.cb) == Importer.SanityStatus.PASS) {
                    system = current;
                } else {
                    notUsedSystems.add(current);
                }
            } catch (ServiceConfigurationError e) {
                log.warn( "Couldn''t load the service...", e);
            }
        }
    }
}
