/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Photo;
import net.wazari.service.PhotoLocal.PhotoRequest;
import net.wazari.service.exchange.ViewSession;
import net.wazari.common.plugins.Importer;

/**
 *
 * @author kevinpouget
 */
@Local
public interface SystemToolsLocal {
    void fullscreen(ViewSession vSession, PhotoRequest rq, String type, Integer id, Integer page);

    void remove(String toString);

    boolean rotate(String type, String ext, String degrees, String source, String dest);

    String shrink(ViewSession vSession, Photo enrPhoto, int width);

    boolean support(String type, String ext);

    boolean thumbnail(String type, String ext, String source, String dest, int height);

    void reloadPlugins(java.lang.String path);

    List<Importer> getPluginList();

}
