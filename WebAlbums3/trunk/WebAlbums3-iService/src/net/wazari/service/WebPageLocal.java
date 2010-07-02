/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.Utilisateur;
import net.wazari.dao.entity.facades.PhotoOrAlbum;
import net.wazari.dao.entity.facades.SubsetOf.Bornes;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.EditMode;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.common.util.XmlBuilder;
import net.wazari.service.exchange.ViewSessionLogin;

/**
 *
 * @author kevin
 */
@Local
@RolesAllowed({UserLocal.VIEWER_ROLE})
public interface WebPageLocal {
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    Bornes calculBornes(Integer page, Integer eltAsked, int taille);

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayListB(Mode mode, ViewSession vSession, Box box) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayListBN(Mode mode, ViewSession vSession, Box box, String name) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayListDroit(Utilisateur right, Integer albmRight) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayListIBT(Mode mode, ViewSession vSession, PhotoOrAlbum entity, Box box) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayListIBTNI(Mode mode, ViewSession vSession, PhotoOrAlbum entity, Box box, String name, String info) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayListLB(Mode mode, ViewSession vSession, List<Tag> ids, Box box) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayListLBNI(Mode mode, ViewSession vSession, List<Tag> ids, Box box, String name, String info) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayMapInBody(ViewSession vSession, String name, String info) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder displayMapInScript(ViewSession vSession, String name, String info) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    EditMode getNextEditionMode(ViewSession vSession);

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlBuilder xmlPage(XmlBuilder from, Bornes bornes) ;

    @PermitAll
    XmlBuilder xmlLogin(ViewSessionLogin vSession) ;

    @PermitAll
    XmlBuilder xmlAffichage(ViewSession vSession) ;

    @RolesAllowed(UserLocal.ADMIN_ROLE)
    void populateEntities();
}
