/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.ejb.Local;
import net.wazari.common.plugins.Importer.Capability;
import net.wazari.dao.entity.Photo;
import net.wazari.service.PhotoLocal.PhotoRequest;
import net.wazari.service.exchange.ViewSession;
import net.wazari.common.plugins.Importer;
import net.wazari.common.plugins.System;

/**
 *
 * @author kevinpouget
 */
@Local
public interface SystemToolsLocal {
    void reloadPlugins(java.lang.String path);

    List<Importer> getPluginList();

    boolean fullscreen(ViewSession vSession, PhotoRequest rq, String type, Integer id, Integer page);

    boolean rotate(String type, String ext, String degrees, String source, String dest);

    String shrink(ViewSession vSession, Photo enrPhoto, int width);

    boolean thumbnail(String type, String ext, String source, String dest, int height);

    boolean supports(String type, String ext, Capability capability);

    System getUsedSystem();

    List<System> getNotUsedSystemList();
}
