/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.AlbumFacadeLocal.Restriction;
import net.wazari.dao.AlbumFacadeLocal.TopFirst;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.entity.facades.SubsetOf;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.dao.exchange.ServiceSession;

/**
 *
 * @author kevin
 */
@Local
public interface CarnetFacadeLocal {
    List<Carnet> findAll();

    void create(Carnet carnet);

    void edit(Carnet carnet);

    void remove(Carnet carnet);

    Carnet find(Integer carnet);

    Carnet newCarnet();

    SubsetOf<Carnet> queryCarnets(ServiceSession vSession, Restriction restriction, TopFirst topFirst, Bornes bornes);

    Carnet loadIfAllowed(ServiceSession session, Integer carnetId);
}
