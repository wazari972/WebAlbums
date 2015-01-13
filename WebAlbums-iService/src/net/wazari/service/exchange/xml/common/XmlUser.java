/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.common;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlUser {
    public String name ;
    @XmlAttribute
    public Integer id;
    @XmlAttribute
    public Boolean selected;
}
