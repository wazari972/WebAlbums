/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

/**
 *
 * @author kevin
 */
public interface ViewSessionCarnet extends ViewSession {
    interface ViewSessionCarnetSubmit extends ViewSessionAlbum {
        String getDesc();

        String getNom();

        String getDate();

        Integer[] getTags();
        boolean getForce();

        boolean getSuppr() ;
        Integer getUserAllowed();
    }

    interface ViewSessionCarnetEdit extends ViewSessionAlbum {

        Integer getCountAlbm();

    }
    interface ViewSessionCarnetDisplay extends ViewSessionAlbum {

        Integer getCountCarnet();

    }
    Integer getId();
    Integer getCount();
    Integer getPage() ;
}
