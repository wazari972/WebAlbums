/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.plugins;

import java.util.List;

/**
 *
 * @author kevinpouget
 */
public interface Importer {
    enum Capability {
        SHRINK, THUMBNAIL, ROTATE, FILE_FULLSCREEN, DIR_FULLSCREEN
    }
    enum SanityStatus {
        FAIL, PASS
    }
    
    String getName() ;
    String getVersion() ;
    String getDescription();
    String getSupportedFilesDesc();
    
    Capability[] supports();

    boolean supports(String type, String ext, Capability cap);

    boolean shrink(ProcessCallback cb, String source, String dest, int width);

    boolean thumbnail(ProcessCallback cb, String source, String dest, int height);

    boolean rotate(ProcessCallback cb, String degrees, String source, String dest);

    void fullscreen(ProcessCallback cb, String path);

    SanityStatus sanityCheck(ProcessCallback cb) ;
}
