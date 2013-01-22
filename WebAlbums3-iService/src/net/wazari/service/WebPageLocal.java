/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.entity.facades.EntityWithId;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.ViewSessionLogin;
import net.wazari.service.exchange.ViewSessionPhoto;
import net.wazari.service.exchange.xml.XmlAffichage;
import net.wazari.service.exchange.xml.XmlPage;
import net.wazari.service.exchange.xml.common.*;
import net.wazari.service.exchange.xml.tag.XmlTag;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE})
public interface WebPageLocal {
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    Bornes calculBornes(Integer page, int taille);

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlWebAlbumsList displayListB(Mode mode, ViewSession vSession, Box box) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlWebAlbumsList displayListBN(Mode mode, ViewSession vSession, Box box) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlUserList displayListDroit(Utilisateur right, Integer albmRight) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlWebAlbumsList displayListIBTD(Mode mode, ViewSession vSession, EntityWithId entity, Box box, String date) throws WebAlbumsServiceException;
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlWebAlbumsList displayListIBT(Mode mode, ViewSession vSession, EntityWithId entity, Box box) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlWebAlbumsList displayListIBTN(Mode mode, ViewSession vSession, EntityWithId entity, Box box) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlWebAlbumsList displayListLB(Mode mode, ViewSession vSession, List<Tag> ids, Box box) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlWebAlbumsList displayListLBN(Mode mode, ViewSession vSession, List<Tag> ids, Box box) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlWebAlbumsList displayMapInScript(ViewSession vSession) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlPage xmlPage(XmlFrom from, Bornes bornes) ;

    @PermitAll
    XmlLoginInfo xmlLogin(ViewSessionLogin vSession) ;

    @PermitAll
    XmlAffichage xmlAffichage(ViewSession vSession) ;

    @PermitAll
    XmlDate xmlDate(String strNewDate) ;

    @RolesAllowed(UserLocal.MANAGER_ROLE)
    void populateEntities();

    @PermitAll
    XmlTag tagListToTagTree(XmlWebAlbumsList tag_used);

    @PermitAll
    XmlWebAlbumsList displayAlbumGeolocations(ViewSessionPhoto vSession) throws WebAlbumsServiceException;
}
