/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import net.wazari.dao.exchange.ServiceSession;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
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

    @RolesAllowed(UtilisateurFacadeLocal.VIEWER_ROLE)
    Map<Tag,Long> queryIDNameCount(ServiceSession session) ;

    List<Tag> queryAllowedTagByType(ServiceSession session, int type) ;

    Tag loadByName(String nom) ;

    List<Tag> loadVisibleTags(ServiceSession sSession, boolean restrictToGeo) ;
    List<Tag> getNoSuchTags(ServiceSession sSession, List<Tag> tags) ;

    List<Tag> findAll(Integer tagId) ;

    Tag find(Integer integer);

    List<Tag> findAll();
}
