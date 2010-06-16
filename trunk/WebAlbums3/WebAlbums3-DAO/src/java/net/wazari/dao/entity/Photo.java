/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
import java.util.List;
import net.wazari.dao.entity.facades.PhotoOrAlbum;

/**
 *
 * @author kevinpouget
 */
public interface Photo extends PhotoOrAlbum, Serializable {
    Album getAlbum();

    String getDate();

    String getDescription();

    Integer getDroit();

    String getExposure();

    String getFlash();

    String getFocal();

    String getHeight();

    Integer getId();

    String getIso();

    String getModel();

    String getPath();

    List<TagPhoto> getTagPhotoList();

    String getType();

    String getWidth();

    void setAlbum(Album album);

    void setDate(String date);

    void setDescription(String description);

    void setDroit(Integer droit);

    void setExposure(String exposure);

    void setFlash(String flash);

    void setFocal(String focal);

    void setHeight(String height);

    void setId(Integer id);

    void setIso(String iso);

    void setModel(String model);

    void setPath(String path);

    void setTagPhotoList(List<TagPhoto> tagPhotoList);

    void setType(String type);

    void setWidth(String width);

}
