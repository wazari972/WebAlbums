/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.util.List;

/**
 *
 * @author kevinpouget
 */
public interface Utilisateur {

    List<Album> getAlbumList();

    Integer getId();

    String getNom();

    void setAlbumList(List<Album> albumList);

    void setId(Integer id);

    void setNom(String nom);

}
