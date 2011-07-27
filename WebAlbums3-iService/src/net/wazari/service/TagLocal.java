/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import net.wazari.service.exchange.xml.tag.XmlTagCloud;
import net.wazari.service.exchange.xml.tag.XmlTagDisplay;
import net.wazari.service.exchange.xml.tag.XmlTagPersonsPlaces;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSessionTag;
import net.wazari.service.exchange.xml.photo.XmlPhotoSubmit;
import net.wazari.service.exchange.xml.tag.XmlTagAbout;

/**
 *
 * @author kevin
 */
@Local
@DeclareRoles({UserLocal.VIEWER_ROLE, UserLocal.MANAGER_ROLE})
public interface TagLocal {    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlTagDisplay treatTagDISPLAY(ViewSessionTag vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlTagPersonsPlaces treatTagPersonsPlaces(ViewSessionTag vSession);

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlTagCloud treatTagCloud(ViewSessionTag vSession);

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlTagAbout treatABOUT(ViewSessionTag vSession);

}
