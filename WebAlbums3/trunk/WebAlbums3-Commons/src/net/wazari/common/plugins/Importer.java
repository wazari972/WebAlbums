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
    enum Capability {
        SHRINK, THUMBNAIL, ROTATE, FULLSCREEN_SINGLE, FULLSCREEN_MULTIPLE, META_DATA
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

    void fullscreenMultiple(ProcessCallback cb, String path);

    void fullscreenFile(ProcessCallback cb, String path);

    boolean setMetadata(Metadata data, String path);

    SanityStatus sanityCheck(ProcessCallback cb) ;

    int getPriority() ;
}
