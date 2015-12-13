/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Local;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.exchange.ServiceSession;

/**
 *
 * @author kevin
 */
@Local
public interface TagFacadeLocal {
    void create(Tag tag);

    void edit(Tag tag);
    
    void remove(Tag tag);

    Tag newTag();
    
    Map<Tag,Long> queryIDNameCount(ServiceSession session) ;

    List<Tag> queryAllowedTagByType(ServiceSession session, int type) ;

    Tag loadByName(String nom) ;

    List<Tag> loadVisibleTags(ServiceSession sSession, boolean restrictToGeo, boolean restrictToTheme) ;
    
    List<Tag> getNoSuchTags(ServiceSession sSession, List<Tag> tags) ;
    
    Tag find(Integer integer);

    List<Tag> findAll();

    Set<Tag> getChildren(Tag tag);
    
    Photo getTagThemePhoto(ServiceSession sSession,Tag enrTag);
}
