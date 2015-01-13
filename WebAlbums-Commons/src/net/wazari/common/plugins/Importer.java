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

    void addBorder(ProcessCallback cb, String imagePath, Integer borderWidth, String color);

    enum Capability {
        SHRINK, THUMBNAIL, ROTATE, META_DATA, ADD_BORDER
    }

    enum SanityStatus {
        FAIL, PASS
    }

    String getName();

    String getVersion();

    String getTargetSystem();

    String getSupportedFilesDesc();

    Capability[] supports();

    boolean supports(String type, String ext, Capability cap);

    boolean shrink(ProcessCallback cb, String source, String dest, int width);

    boolean thumbnail(ProcessCallback cb, String source, String dest, int height);

    boolean rotate(ProcessCallback cb, String degrees, String source, String dest);

    boolean setMetadata(Metadata data, String path);

    SanityStatus sanityCheck(ProcessCallback cb);

    int getPriority();

    interface Metadata {

        void setExposure(String exposure);

        String getExposure();

        void setFlash(String flash);

        String getFlash();

        void setFocal(String focal);

        String getFocal();

        void setHeight(String height);

        String getHeight();

        void setIso(String iso);

        String getIso();

        void setModel(String model);

        String getModel();

        void setWidth(String width);

        String getWidth();

        void setDate(String date);

        String getDate();
    }

    interface ProcessCallback {
        void exec(String[] cmd);
        int execWaitFor(String[] cmd);
    }
}
