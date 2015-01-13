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
    @XmlElement(name = "tagInfo")
    public List<XmlTagCloudEntry> parentList = new LinkedList<>();

    public static class XmlTagCloudEntry {
        @XmlElement
        public XmlTag tag;
        @XmlAttribute
        public int size;
        @XmlAttribute
        public Long nb;
        @XmlAttribute
        public String type;
        
        @XmlElementWrapper(name="children")
        @XmlElement(name="tagInfo")
        public List<XmlTagCloudEntry> children;
    }
}
