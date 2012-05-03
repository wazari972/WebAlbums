/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.text.SimpleDateFormat;
import java.util.List;
import net.wazari.dao.entity.facades.EntityWithId;

/**
 *
 * @author kevinpouget
 */
public interface Carnet extends EntityWithId {
    SimpleDateFormat DATE_STANDARD = new SimpleDateFormat("yyyy-MM-dd");

    Integer getId();
    void setId(Integer id);
    
    String getDate();
    void setDate(String date);
    
    String getDescription();
    void setDescription(String description);
    
    String getText();
    void setText(String text);
    
    String getNom();
    void setNom(String nom);

    List<Photo> getPhotoList();
    void setPhotoList(List<Photo> photoList);
    
    List<Album> getAlbumList();
    void setAlbumList(List<Album> enrAlbums);
    
    Photo getPicture();
    void setPicture(Photo picture);

    Theme getTheme();
    void setTheme(Theme theme);
    
    Utilisateur getDroit();
    void setDroit(Utilisateur droit);    
}
