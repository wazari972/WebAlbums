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
public interface Theme extends Serializable {

    List<Album> getAlbumList();

    Integer getId();

    String getNom();

    String getPassword();

    List<TagTheme> getTagThemeList();

    void setAlbumList(List<Album> albumList);

    void setId(Integer id);

    void setNom(String nom);

    void setPassword(String password);

    void setTagThemeList(List<TagTheme> tagThemeList);

}
