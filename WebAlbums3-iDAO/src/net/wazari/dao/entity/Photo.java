/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
import java.util.List;
import net.wazari.common.plugins.Importer.Metadata;
import net.wazari.dao.entity.facades.EntityWithId;

/**
 *
 * @author kevinpouget
 */
public interface Photo extends Serializable, EntityWithId, Metadata {
    Album getAlbum();

    String getDescription();

    Integer getDroit();

    Integer getId();

    String getPath();

    List<TagPhoto> getTagPhotoList();

    String getType();
    
    List<Carnet> getCarnetList() ;
    
    Integer getStars();

    void setAlbum(Album album);

    void setDescription(String description);

    void setDroit(Integer droit);

    void setId(Integer id);

    void setPath(String path);

    void setTagPhotoList(List<TagPhoto> tagPhotoList);

    void setType(String type);

    void setCarnetList(List<Carnet> jPACarnetList) ;
    
    void setStars(Integer stars);
}
