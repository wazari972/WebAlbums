/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.plugins;

/**
 *
 * @author kevinpouget
 */
public interface Importer {
    String getName() ;
    
    boolean support(String type, String ext);

    boolean shrink(ProcessCallback cb, String source, String dest, int width);

    boolean thumbnail(ProcessCallback cb, String source, String dest, int height);

    boolean rotate(ProcessCallback cb, String degrees, String source, String dest);

    void fullscreen(ProcessCallback cb, String path);
}
