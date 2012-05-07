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
    Integer getId();
    void setId(Integer id);
    
    String getNom();
    void setNom(String nom);
    
    List<TagTheme> getTagThemeList();
    void setTagThemeList(List<TagTheme> tagThemeList);

    Photo getPicture() ;
    void setPicture(Photo picture) ;
    
    Photo getBackground() ;
    void setBackground(Photo background) ;
    
    List<Album> getAlbumList();
    void setAlbumList(List<Album> carnetList);
    
    List<Album> getCarnetList();
    void setCarnetList(List<Carnet> carnetList);
}
