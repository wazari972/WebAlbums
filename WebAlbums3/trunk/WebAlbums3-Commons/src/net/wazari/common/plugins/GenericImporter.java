/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.plugins;

/**
 *
 * @author kevinpouget
 */
public abstract class GenericImporter implements Importer{
    public boolean shrink(ProcessCallback cb, String source, String dest, int width) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean thumbnail(ProcessCallback cb, String source, String dest, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean rotate(ProcessCallback cb, String degrees, String source, String dest) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fullscreenMultiple(ProcessCallback cb, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void fullscreenFile(ProcessCallback cb, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setMetadata(Metadata data, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addBorder(ProcessCallback cb, String imagePath, Integer borderWidth, String color) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTargetSystem() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSupportedFilesDesc() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Capability[] supports() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean supports(String type, String ext, Capability cap) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SanityStatus sanityCheck(ProcessCallback cb) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPriority() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
