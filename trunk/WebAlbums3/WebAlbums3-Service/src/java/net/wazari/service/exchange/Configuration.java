/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface Configuration {

    Integer autoLogin();

    String getData();

    String getDataPath();

    String getFTP();

    String getImages();

    String getMini();

    String getSourcePath();

    String getSourceURL();

    String getTempDir();

    boolean hasInternet();

    boolean isReadOnly();

    boolean isSgbdHsqldb();

    boolean isSgbdMysql();

    boolean lightenDb();

    boolean wantsQueries();

    boolean wantsStats();

    boolean wantsXsl();

    String getSep();

    int getAlbumSize() ;
    int getPhotoSize() ;

}
