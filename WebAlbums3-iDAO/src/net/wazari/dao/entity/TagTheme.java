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
    Boolean getIsVisible();

    Photo getPhoto();

    Tag getTag();

    Theme getTheme();

    void setIsVisible(Boolean isVisible);

    void setPhoto(Photo photo);

    void setTag(Tag tag);

    void setTheme(Theme theme);

}
