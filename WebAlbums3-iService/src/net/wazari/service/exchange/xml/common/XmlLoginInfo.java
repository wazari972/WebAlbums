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
public class XmlLoginInfo {
    public Integer themeid;
    public String theme;
    @XmlAttribute
    public Boolean root;
    @XmlAttribute
    public Boolean admin;
    public String user;
    public String role;

}
