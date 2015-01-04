/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange;

/**
 *
 * @author pk033
 */
public interface Configuration {
    boolean isPathURL() ;

    String getImagesPath(boolean withRoot);

    String getFtpPath();

    String getMiniPath(boolean withRoot);

    String getRootPath();

    String getBackupPath();

    String getTempPath();

    String getConfigFilePath() ;

    String getPluginsPath();

    boolean isReadOnly();

    String getSep();

    boolean wantsProtectDB() ;
    
    Class getConfClass();
    Object getConf();
    void setConf(Object conf);
}
