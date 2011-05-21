/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.util.system;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import net.wazari.common.plugins.Importer;
import net.wazari.common.util.ClassPathUtil;
import net.wazari.service.PluginManagerLocal;
import net.wazari.common.plugins.System;

/**
 *
 * @author kevinpouget
 */
@Singleton
public class PluginManager implements PluginManagerLocal {

    private static final Logger log = Logger.getLogger(PluginManager.class.getCanonicalName());
    private final List<Importer> validWrappers = new LinkedList<Importer>();
    private final List<Importer> invalidWrappers = new LinkedList<Importer>();
    private System system = null;
    private final List<System> notUsedSystems = new LinkedList<System>();

    @Override
    public List<Importer> getPluginList() {
        List<Importer> wrappers = new LinkedList<Importer>(validWrappers);
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

    public void reloadPlugins(String path) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader() ;
        if (path != null) {
            log.log(Level.INFO, "+++Adding plugins from {0}", path);
            cl = ClassPathUtil.addDirToClasspath(new File(path));
        }
        validWrappers.clear();
        invalidWrappers.clear();
        log.log(Level.INFO, "+++ Loading services for \"{0}\"", Importer.class.getCanonicalName());
        ServiceLoader<Importer> servicesImg = ServiceLoader.load(Importer.class, cl);
        Iterator<Importer> itImg = servicesImg.iterator();
        while (itImg.hasNext()) {
            try {
                Importer current = itImg.next();
                log.log(Level.INFO, "+++ Adding \"{0}\"", current.getClass().getCanonicalName());
                if (current.sanityCheck(SystemTools.cb) == Importer.SanityStatus.PASS) {
                    validWrappers.add(current);
                } else {
                    invalidWrappers.add(current);
                }
            } catch (ServiceConfigurationError e) {
                log.log(Level.WARNING, "Couldn''t load the service...", e);
            }
        }

        Collections.sort(validWrappers, new Comparator<Importer>() {

            public int compare(Importer o1, Importer o2) {

                return o2.getPriority() - o1.getPriority();
            }
        });

        log.log(Level.INFO, "+++ Loading services for \"{0}\"", System.class.getCanonicalName());
        ServiceLoader<System> servicesSys = ServiceLoader.load(System.class, cl);
        this.system = null;
        Iterator<System> itSyst = servicesSys.iterator();
        while (itSyst.hasNext()) {
            try {
                System current = itSyst.next();
                log.log(Level.INFO, "+++ Adding \"{0}\"", current.getClass().getCanonicalName());
                if (system == null && current.sanityCheck(SystemTools.cb) == Importer.SanityStatus.PASS) {
                    system = current;
                } else {
                    notUsedSystems.add(current);
                }
            } catch (ServiceConfigurationError e) {
                log.log(Level.WARNING, "Couldn''t load the service...", e);
            }
        }
    }
}
