/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.util.system;

import java.io.File;
import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Photo;
import net.wazari.service.exchange.ViewSession;

/**
 *
 * @author kevin
 */
@Local
public interface SystemToolsService {

    @SuppressWarnings(value = "unchecked")
    void fullscreen(ViewSession vSession, List<Photo> lstPhoto, String type, Integer id, Integer page);

    boolean link(String source, File dest);

    void remove(String file);

    boolean rotate(String type, String ext, String degrees, String source, String dest);

    String shrink(ViewSession vSession, Photo enrPhoto, int width);

    boolean support(String type, String ext);

    boolean thumbnail(String type, String ext, String source, String dest, int height);

}
