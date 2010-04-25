/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.dao;

import net.wazari.dao.exchange.ServiceSession;
import java.util.List;
import javax.ejb.Local;
import net.wazari.dao.entity.Photo;

/**
 *
 * @author kevin
 */
@Local
public interface PhotoFacadeLocal {

    void create(Photo photo);

    void edit(Photo photo);

    void remove(Photo photo);

    Photo find(Object id);

    List<Photo> findAll();

    Photo loadIfAllowed(ServiceSession session, int id);

    List<Photo> loadFromAlbum(ServiceSession session, int albumId, Integer first);

    Photo loadByPath(String path);

    List<Photo> loadByTags(ServiceSession session, List<Integer> listTagId, Integer first);
}
