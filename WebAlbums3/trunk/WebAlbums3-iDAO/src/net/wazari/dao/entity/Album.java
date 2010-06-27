/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.util.List;
import net.wazari.dao.entity.facades.PhotoOrAlbum;

/**
 *
 * @author kevinpouget
 */
public interface Album extends PhotoOrAlbum {

    String getDate();

    String getDescription();

    Utilisateur getDroit();

    Integer getId();

    String getNom();

    List<Photo> getPhotoList();

    Integer getPicture();

    Theme getTheme();

    void setDate(String date);

    void setDescription(String description);

    void setDroit(Utilisateur droit);

    void setId(Integer id);

    void setNom(String nom);

    void setPhotoList(List<Photo> photoList);

    void setPicture(Integer picture);

    void setTheme(Theme theme);

}
