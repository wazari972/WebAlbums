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
public interface TagTheme extends Serializable {

    Integer getId();

    Boolean getIsVisible();

    Integer getPhoto();

    Tag getTag();

    Theme getTheme();

    void setId(Integer id);

    void setIsVisible(Boolean isVisible);

    void setPhoto(Integer photo);

    void setTag(Tag tag);

    void setTheme(Theme theme);

}
