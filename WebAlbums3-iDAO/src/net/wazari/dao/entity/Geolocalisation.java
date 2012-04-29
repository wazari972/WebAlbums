/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;


/**
 *
 * @author kevinpouget
 */
public interface Geolocalisation extends Serializable {
    Integer getTag();
    void setTag(Integer tag);
            
    String getLat();
    void setLat(String lat);
    
    String getLongitude();
    void setLongitude(String longitude);
}
