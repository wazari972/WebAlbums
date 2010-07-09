/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.common.plugins;

/**
 *
 * @author kevinpouget
 */
public interface Metadata {
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
