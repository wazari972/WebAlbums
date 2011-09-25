/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
import java.util.List;
import net.wazari.dao.entity.facades.EntityWithId;

/**
 *
 * @author kevinpouget
 */
public interface Tag extends Serializable, EntityWithId {

    Geolocalisation getGeolocalisation();

    Person getPerson();
    
    Integer getId();

    String getNom();

    List<TagPhoto> getTagPhotoList();

    List<TagTheme> getTagThemeList();

    int getTagType();

    void setGeolocalisation(Geolocalisation geolocalisation);
    
    void setPerson(Person person);

    void setId(Integer id);

    void setNom(String nom);

    void setTagPhotoList(List<TagPhoto> tagPhotoList);

    void setTagThemeList(List<TagTheme> tagThemeList);

    void setTagType(int tagType);

    Tag getParent() ;
    void setParent(Tag parent) ;

    List<Tag> getSonList() ;
    void setSonList(List<Tag> sonList) ;
}
