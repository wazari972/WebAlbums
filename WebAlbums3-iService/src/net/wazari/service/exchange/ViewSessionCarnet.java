/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import java.util.Set;

/**
 *
 * @author kevin
 */
public interface ViewSessionCarnet extends ViewSession {
    interface ViewSessionCarnetSubmit extends ViewSessionAlbum {
        Integer getCarnet();
        
        String getDesc();

        String getNom();

        String getDate();

        Integer[] getTags();
        boolean getForce();

        boolean getSuppr() ;
        Integer getUserAllowed();

        String getCarnetText();

        Set<Integer> getCarnetPhoto();

        Integer getCarnetRepr();

        Set<Integer> getCarnetAlbum();
    }

    interface ViewSessionCarnetEdit extends ViewSessionAlbum {
        Integer getCarnetsPage();
        Integer getCarnet();

    }
    interface ViewSessionCarnetDisplay extends ViewSessionAlbum {
        Integer getCarnet();
        Integer getCarnetsPage();
    }
    Integer getId();
    Integer getPage() ;
}
