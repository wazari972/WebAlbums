/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao;

import java.util.Collection;
import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.dao.exchange.ServiceSession;
import net.wazari.dao.exchange.ServiceSession.ListOrder;

/**
 *
 * @author kevin
 */
@Local
public interface PhotoFacadeLocal {
    void create(Photo photo);

    void edit(Photo photo);

    void remove(Photo photo);

    Photo loadIfAllowed(ServiceSession session, int id);
    
    Photo loadByPathIfAllowed(ServiceSession session, String path);

    SubsetOf<Photo> loadFromAlbum(ServiceSession session, Album album, Bornes bornes, ListOrder order);

    Photo loadByPath(String path);

    SubsetOf<Photo> loadByTags(ServiceSession session, Collection<Tag> listTagId, Bornes bornes, ListOrder order);

    Photo find(Integer photoID);

    Photo newPhoto();

    List<Photo> findAll();

    Photo loadRandom(ServiceSession vSession);

    void pleaseFlush();
}
