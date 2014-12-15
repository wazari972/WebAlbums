/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import net.wazari.service.exception.WebAlbumsServiceException;
import net.wazari.service.exchange.ViewSession;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagDisplay;
import net.wazari.service.exchange.ViewSessionTag.ViewSessionTagSimple;
import net.wazari.service.exchange.xml.photo.XmlPhotoSubmit;
import net.wazari.service.exchange.xml.tag.XmlTagAbout;
import net.wazari.service.exchange.xml.tag.XmlTagCloud;
import net.wazari.service.exchange.xml.tag.XmlTagDisplay;
import net.wazari.service.exchange.xml.tag.XmlTagPersonsPlaces;

/**
 *
 * @author kevin
 */
@Local
public interface TagLocal {    
    XmlTagDisplay treatTagDISPLAY(ViewSessionTagDisplay vSession, XmlPhotoSubmit submit) throws WebAlbumsServiceException;

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlTagPersonsPlaces treatTagPersons(ViewSession vSession);
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlTagPersonsPlaces treatTagPlaces(ViewSession vSession);
    
    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlTagCloud treatTagCloud(ViewSession vSession);

    @RolesAllowed(UserLocal.VIEWER_ROLE)
    XmlTagAbout treatABOUT(ViewSessionTagSimple vSession);

}
