/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.tag;

import javax.xml.bind.annotation.XmlAttribute;

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
    @XmlAttribute
    public String lat;
    @XmlAttribute
    public String longit;
    @XmlAttribute
    public Boolean checked;

}
