/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.photo.XmlPhotoEdit;
import net.wazari.service.exchange.xml.tag.XmlTagAbout;
import net.wazari.service.exchange.xml.tag.XmlTagCloud;
import net.wazari.service.exchange.xml.tag.XmlTagDisplay;
import net.wazari.service.exchange.xml.tag.XmlTagPersonsPlaces;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlTags {
    public XmlTagCloud cloud;
    public XmlTagPersonsPlaces places;
    public XmlTagPersonsPlaces persons;
    public XmlTagAbout about;
    
    public XmlPhotoEdit edit;
    public XmlTagDisplay display;
    public XmlReturnTo return_to;

}
