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
    String getDescr();

    String getNom();

    String getDate();

    Integer[] getTags();

    Boolean getForce();

    Boolean getSuppr() ;

    Integer getPage() ;

    Integer getCount();

    Integer getCountAlbm();

    Integer getUserAllowed();
}
