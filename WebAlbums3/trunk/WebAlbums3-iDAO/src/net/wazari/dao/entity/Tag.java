/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author kevinpouget
 */
public interface Tag extends Serializable {

    Geolocalisation getGeolocalisation();

    Integer getId();

    String getNom();

    List<TagPhoto> getTagPhotoList();

    List<TagTheme> getTagThemeList();

    int getTagType();

    void setGeolocalisation(Geolocalisation geolocalisation);

    void setId(Integer id);

    void setNom(String nom);

    void setTagPhotoList(List<TagPhoto> tagPhotoList);

    void setTagThemeList(List<TagTheme> tagThemeList);

    void setTagType(int tagType);

}
