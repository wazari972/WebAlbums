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

    String getDate();

    String getDescription();
    
    String getText();

    Utilisateur getDroit();

    Integer getId();

    String getNom();

    List<Photo> getPhotoList();
    
    List<Album> getAlbumList();

    Integer getPicture();

    Theme getTheme();

    void setDate(String date);

    void setDescription(String description);
    
    void setText(String text);

    void setDroit(Utilisateur droit);

    void setId(Integer id);

    void setNom(String nom);

    void setPhotoList(List<Photo> photoList);

    void setPicture(Integer picture);

    void setTheme(Theme theme);

    void setAlbumList(List<Album> enrAlbums);
}
