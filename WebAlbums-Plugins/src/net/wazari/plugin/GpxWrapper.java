/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.plugin;

import net.wazari.common.plugins.GenericImporter;

/**
 *
 * @author kevin
 */
public class GpxWrapper extends GenericImporter {
    @Override
    public String getName() {
        return "Simple GPX Importer" ;
    }
    
    @Override
    public String getVersion() {
        return "1" ;
    }
    
    @Override
    public Capability[] supports() {
        return new Capability[] {Capability.THUMBNAIL} ;
    }
    
    @Override
    public boolean supports(String type, String ext, Capability cap) {
        return (ext != null && ext.equals("gpx") && cap == Capability.THUMBNAIL);
    }
    
    @Override
    public boolean thumbnail(ProcessCallback cb, String source, String dest, int height) {
        return true;
    }
    
    public SanityStatus sanityCheck(ProcessCallback cb) {
        return SanityStatus.PASS;
    }

    public int getPriority() {
        return -10 ;
    }
}
