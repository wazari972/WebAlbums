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

    String getLat();

    String getLongitude();

    Integer getTag();

    Tag getTag1();

    void setLat(String lat);

    void setLongitude(String longitude);

    void setTag(Integer tag);

    void setTag1(Tag tag1);

}
