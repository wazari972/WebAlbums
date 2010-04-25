/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface ViewSessionAlbum extends ViewSession {
    String getDesc();

    String getNom();

    String getDate();

    Integer[] getTags();

    boolean getForce();

    boolean getSuppr() ;

    Integer getPage() ;

    Integer getCount();

    Integer getCountAlbm();

    Integer getUserAllowed();
}
