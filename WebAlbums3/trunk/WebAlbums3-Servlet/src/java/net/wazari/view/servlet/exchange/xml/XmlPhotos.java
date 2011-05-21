/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.photo.XmlPhotoAbout;
import net.wazari.service.exchange.xml.photo.XmlPhotoDisplay;
import net.wazari.service.exchange.xml.photo.XmlPhotoEdit;
import net.wazari.service.exchange.xml.photo.XmlPhotoRandom;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPhotos {
    public XmlPhotoRandom random;
    public XmlPhotoEdit edit;
    public XmlReturnTo return_to;
    public XmlPhotoDisplay display;
    public XmlPhotoAbout about;
}
