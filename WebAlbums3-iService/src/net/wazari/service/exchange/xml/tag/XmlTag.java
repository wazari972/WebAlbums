/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.tag;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author kevin
 */
public class XmlTag {
    public String name;
    @XmlAttribute
    public Integer id;
    @XmlAttribute
    public Integer picture;
    @XmlElement
    public String picturePath;
    @XmlAttribute
    public Boolean checked;
    @XmlAttribute
    public Boolean minor;

}
