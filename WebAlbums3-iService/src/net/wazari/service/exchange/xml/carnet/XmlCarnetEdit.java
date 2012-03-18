/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.carnet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.common.XmlUserList;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlCarnetEdit extends XmlInfoException{
    public XmlCarnetSubmit submit;
    @XmlAttribute
    public Integer picture;
    public String name;
    @XmlAttribute
    public Integer page;
    @XmlAttribute
    public Integer id;
    public String description;
    public String date;
    public XmlUserList rights;
    public String text;
}
