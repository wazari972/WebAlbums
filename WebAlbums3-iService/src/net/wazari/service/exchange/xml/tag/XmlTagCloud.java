/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.tag;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 *
 * @author kevin
 */
public class XmlTagCloud {
    @XmlElement(name = "tag")
    public List<XmlTag> parentList = new LinkedList<XmlTag>();

    public static class XmlTagCloudEntry extends XmlTag {        
        @XmlAttribute
        public String type = null;
        @XmlAttribute
        public int size;
        @XmlAttribute
        public Long nb;
    }
}
