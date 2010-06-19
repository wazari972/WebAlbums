/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;

/**
 *
 * @author kevinpouget
 */
public interface TagPhoto extends Serializable {

    Long getId();

    Photo getPhoto();

    Tag getTag();

    void setId(Long id);

    void setPhoto(Photo photo);

    void setTag(Tag tag);

}
