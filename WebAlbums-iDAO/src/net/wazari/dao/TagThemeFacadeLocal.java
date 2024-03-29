/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.TagTheme;

/**
 *
 * @author kevin
 */
@Local
public interface TagThemeFacadeLocal {
    void create(TagTheme tagTheme);

    void edit(TagTheme tagTheme);

    void remove(TagTheme tagTheme);

    TagTheme loadByTagTheme(Integer tagID, Integer themeID) ;

    TagTheme newTagTheme();

    List<TagTheme> findAll();
}
