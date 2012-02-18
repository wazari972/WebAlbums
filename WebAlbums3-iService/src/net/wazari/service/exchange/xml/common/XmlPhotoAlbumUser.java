/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange.xml.common;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPhotoAlbumUser {
    public XmlPhotoAlbumUser(){}
    public XmlPhotoAlbumUser(String name, Boolean outside) {
        this.name = name;
        this.outside = outside;
    }
    @XmlAttribute
    Boolean outside;
    @XmlValue
    String name;
}