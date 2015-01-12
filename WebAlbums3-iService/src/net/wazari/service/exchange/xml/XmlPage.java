/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml;

import java.util.LinkedList;
import java.util.List;
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

    public List<Integer> prev = new LinkedList<>();
    @XmlAttribute
    public Integer current;

    public List<Integer> next = new LinkedList<>();
    @XmlAttribute
    public Integer last;
    @XmlAttribute
    public Integer previ;
    @XmlAttribute
    public Integer nexti;

}
