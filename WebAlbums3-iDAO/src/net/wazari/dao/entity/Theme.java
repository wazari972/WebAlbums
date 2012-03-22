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
public interface Theme extends Serializable, EntityWithId {

    List<Album> getAlbumList();

    Integer getId();

    String getNom();

    List<TagTheme> getTagThemeList();

    void setAlbumList(List<Album> albumList);

    void setId(Integer id);

    void setNom(String nom);

    void setTagThemeList(List<TagTheme> tagThemeList);

    Photo getPicture() ;

    void setPicture(Photo picture) ;
    
    Photo getBackground() ;

    void setBackground(Photo background) ;
}
