/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.photo.XmlPhotoId;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlTheme {
    @XmlAttribute
    public String name ;
    @XmlAttribute
    public Integer id ;
    public XmlPhotoId picture;
}
