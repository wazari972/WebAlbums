/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao.entity;

import java.io.Serializable;

/**
 *
 * @author kevin
 */
public interface Gpx extends Serializable {
    Integer getId();
    
    Album getAlbum();

    String getDescription();

    String getGpxPath();
    
    void setId(Integer id);

    void setAlbum(Album album);

    void setDescription(String description);

    void setGpxPath(String gpxPath);
}