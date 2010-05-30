/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import net.wazari.dao.exchange.ServiceSession;
import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
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

    void deleteByPhoto(Photo enrPhoto) ;

    List<TagPhoto> queryByAlbum(Album album) ;

    TagPhoto loadByTagPhoto(int tagID, int photoId) ;

    List<Tag> selectDistinctTags() ;
    List<Tag> selectUnusedTags(ServiceSession sSession) ;
}
