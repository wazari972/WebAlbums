/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange;

import java.util.Set;
import net.wazari.service.exchange.ViewSession.Carnet_Action;
import net.wazari.service.exchange.ViewSession.Carnet_Special;

/**
 *
 * @author kevin
 */
public interface ViewSessionCarnet {
    interface ViewSessionCarnetSubmit {
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

    interface ViewSessionCarnetEdit {
        Integer getCarnetsPage();
        Integer getCarnet();
        Integer getPage() ;
    }
    interface ViewSessionCarnetDisplay {
        Integer getCarnet();
        Integer getCarnetsPage();
        Integer getPage() ;
        ViewSession getVSession();
    }
    Carnet_Special getSpecial();
    Carnet_Action getAction();
    Integer getId();
    
    ViewSession getVSession();
}
