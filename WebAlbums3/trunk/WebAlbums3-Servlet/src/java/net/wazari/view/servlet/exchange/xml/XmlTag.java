/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import net.wazari.service.exchange.xml.photo.XmlPhotoEdit;
import net.wazari.service.exchange.xml.tag.XmlTagCloud;
import net.wazari.service.exchange.xml.tag.XmlTagDisplay;
import net.wazari.service.exchange.xml.tag.XmlTagPersonsPlaces;

/**
 *
 * @author kevin
 */
public class XmlTag {
    public XmlTagCloud cloud;
    public XmlTagPersonsPlaces personsPlaces;
    public XmlPhotoEdit edit;
    public XmlTagDisplay display;
    public XmlReturnTo returnTo;

}
