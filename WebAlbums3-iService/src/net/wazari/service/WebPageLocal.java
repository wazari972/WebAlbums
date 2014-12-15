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
import net.wazari.service.exchange.ViewSession.Tag_Mode;
import net.wazari.service.exchange.ViewSessionCarnet.ViewSessionCarnetSimple;
import net.wazari.service.exchange.ViewSessionLogin;
import net.wazari.service.exchange.ViewSessionPhoto.ViewSessionAnAlbum;
import net.wazari.service.exchange.xml.XmlAffichage;
import net.wazari.service.exchange.xml.XmlPage;
import net.wazari.service.exchange.xml.common.*;
import net.wazari.service.exchange.xml.tag.XmlTag;

/**
 *
 * @author kevin
 */
@Local
public interface WebPageLocal {
    Bornes calculBornes(Integer page, int taille);

    XmlWebAlbumsList displayListB(Tag_Mode mode, ViewSession vSession, Box box) throws WebAlbumsServiceException;

    XmlWebAlbumsList displayListBN(Tag_Mode mode, ViewSession vSession, Box box) throws WebAlbumsServiceException;

    XmlUserList displayListDroit(Utilisateur right, Integer albmRight) throws WebAlbumsServiceException;

    XmlWebAlbumsList displayListIBTD(Tag_Mode mode, ViewSession vSession, EntityWithId entity, Box box, String date) throws WebAlbumsServiceException;
    
    XmlWebAlbumsList displayListIBT(Tag_Mode mode, ViewSession vSession, EntityWithId entity, Box box) throws WebAlbumsServiceException;

    XmlWebAlbumsList displayListIBTN(Tag_Mode mode, ViewSession vSession, EntityWithId entity, Box box) throws WebAlbumsServiceException;

    XmlWebAlbumsList displayListLB(Tag_Mode mode, ViewSession vSession, List<Tag> ids, Box box) throws WebAlbumsServiceException;

    XmlWebAlbumsList displayListLBN(Tag_Mode mode, ViewSession vSession, List<Tag> ids, Box box) throws WebAlbumsServiceException;

    XmlWebAlbumsList displayMapInScript(ViewSession vSession) throws WebAlbumsServiceException;

    XmlPage xmlPage(XmlFrom from, Bornes bornes) ;

    XmlLoginInfo xmlLogin(ViewSessionLogin vSession) ;

    XmlAffichage xmlAffichage(ViewSession vSession) ;

    XmlDate xmlDate(String strNewDate) ;

    void populateEntities();

    XmlTag tagListToTagTree(XmlWebAlbumsList tag_used);

    XmlWebAlbumsList displayAlbumGeolocations(ViewSessionAnAlbum vSession) throws WebAlbumsServiceException;

    XmlWebAlbumsList displayCarnetGeolocations(ViewSessionCarnetSimple vSession) throws WebAlbumsServiceException;
}
