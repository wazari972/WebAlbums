/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import net.wazari.dao.exchange.ServiceSession;
import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;

/**
 *
 * @author kevin
 */
@Local
public interface TagPhotoFacadeLocal {

    void create(TagPhoto tagPhoto);

    void edit(TagPhoto tagPhoto);

    void remove(TagPhoto tagPhoto);

    TagPhoto find(Object id);

    List<TagPhoto> findAll();

    void deleteByPhoto(int photoID) ;

    List<TagPhoto> queryByPhoto(int photoID) ;

    List<TagPhoto> queryByAlbum(int albumID) ;

    TagPhoto loadByTagPhoto(int tagID, int photoID) ;

    List<TagPhoto> queryByTag(int tagID)  ;

    List<Tag> selectDistinctTags() ;
    List<Tag> selectUnusedTags(ServiceSession sSession) ;
}
