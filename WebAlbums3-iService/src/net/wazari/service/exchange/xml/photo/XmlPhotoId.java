/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPhotoId {
    public XmlPhotoId(){};
    @XmlAttribute
    public Integer id;
    @XmlValue
    public String path;
    
    public XmlPhotoId(Integer id) {
        this.id = id;
    }
}
