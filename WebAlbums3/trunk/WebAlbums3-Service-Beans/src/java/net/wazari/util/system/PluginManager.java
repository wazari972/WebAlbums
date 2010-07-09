/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import net.wazari.common.plugins.Importer;
import net.wazari.common.util.ClassPathUtil;
import net.wazari.service.PluginManagerLocal;
import net.wazari.common.plugins.System ;
/**
 *
 * @author kevinpouget
 */
@Stateful
public class PluginManager implements PluginManagerLocal{
    private static final Logger log = Logger.getLogger(PluginManager.class.getCanonicalName());

    private final List<Importer> validWrappers = new LinkedList<Importer>();
    private final List<Importer> invalidWrappers = new LinkedList<Importer>();
    private System system = null ;
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
        return notUsedSystems ;
    }

    @Override
    public System getUsedSystem() {
        return system ;
    }

    public void reloadPlugins(String path) {
        if (path != null) {
            ClassPathUtil.addDirToClasspath(new File(path));
        }
        validWrappers.clear();
        invalidWrappers.clear();
        log.log(Level.INFO, "+++ Loading services for \"{0}\"", Importer.class.getCanonicalName());
        ServiceLoader<Importer> servicesImg = ServiceLoader.load(Importer.class);
        for (Importer current : servicesImg) {
            log.log(Level.INFO, "+++ Adding \"{0}\"", current.getClass().getCanonicalName());
            if (current.sanityCheck(SystemTools.cb) == Importer.SanityStatus.PASS) {
                validWrappers.add(current);
            } else {
                invalidWrappers.add(current);
            }
        }

        Importer[] sortedWrappers = validWrappers.toArray(new Importer[validWrappers.size()]);
        Arrays.sort(sortedWrappers, new Comparator<Importer>() {

            public int compare(Importer o1, Importer o2) {

                return o1.getPriority() - o2.getPriority() ;
            }
        }) ;
        validWrappers.clear() ;
        validWrappers.addAll(Arrays.asList(sortedWrappers)) ;

        log.log(Level.INFO, "+++ Loading services for \"{0}\"", System.class.getCanonicalName());
        ServiceLoader<System> servicesSys = ServiceLoader.load(System.class);
        this.system = null ;
        for (System current : servicesSys) {
            log.log(Level.INFO, "+++ Adding \"{0}\"", current.getClass().getCanonicalName());
            if (system == null && current.sanityCheck(SystemTools.cb) == Importer.SanityStatus.PASS) {
                system = current;
            } else {
                notUsedSystems.add(current);
            }
        }
    }
}
