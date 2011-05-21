/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.ejb.Local;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.System;

/**
 *
 * @author kevinpouget
 */
@Local
public interface PluginManagerLocal {

    void reloadPlugins(String pluginsPath);

    List<Importer> getPluginList();

    System getUsedSystem();

    List<System> getNotUsedSystemList();

    List<Importer> getWorkingPlugins();
}
