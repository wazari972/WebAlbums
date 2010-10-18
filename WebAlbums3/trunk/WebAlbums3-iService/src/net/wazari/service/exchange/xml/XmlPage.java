/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlFrom;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPage {
    public String description;
    public XmlFrom url;
    @XmlAttribute
    public Integer first;
    @XmlAttribute
    public Integer prev;
    @XmlAttribute
    public Integer current;
    @XmlAttribute
    public Integer next;
    @XmlAttribute
    public Integer last;
    @XmlAttribute
    public Integer previ;
    @XmlAttribute
    public Integer nexti;

}
