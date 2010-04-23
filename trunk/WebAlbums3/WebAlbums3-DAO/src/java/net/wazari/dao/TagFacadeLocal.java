/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import net.wazari.dao.exchange.ServiceSession;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import net.wazari.dao.entity.Tag;

/**
 *
 * @author kevin
 */
@Local
public interface TagFacadeLocal {

    void create(Tag tag);

    void edit(Tag tag);

    void remove(Tag tag);

    Tag find(Object id);

    List<Tag> findAll();
    
    Map<Tag,Long> queryIDNameCount(ServiceSession session) ;

    List<Tag> queryAllowedTagByType(ServiceSession session, int type) ;

    Tag loadByName(String nom) ;

    List<Tag> loadVisibleTags(ServiceSession sSession, boolean restrictToGeo) ;
    List<Tag> getNoSuchTags(ServiceSession sSession, List<Tag> tags) ;
}
