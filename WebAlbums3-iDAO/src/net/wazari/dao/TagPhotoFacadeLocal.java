/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.*;

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

    TagPhoto newTagPhoto();

    List<TagPhoto> findAll();
    
    List<TagPhoto> queryByCarnet(Carnet carnet);

}
