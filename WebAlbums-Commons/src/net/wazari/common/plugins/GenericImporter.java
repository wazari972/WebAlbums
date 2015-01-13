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

    public String getTargetSystem() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSupportedFilesDesc() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
