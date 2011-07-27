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
    public List<XmlTagCloudEntry> parentList = new LinkedList<XmlTagCloudEntry>() ;

    public static class XmlTagCloudEntry {
        @XmlAttribute
        public int size;
        @XmlAttribute
        public Long nb;
        @XmlAttribute
        public Integer id;
        public String name;
        @XmlElementWrapper(name = "children")
        public List<XmlTagCloudEntry> tag = new LinkedList<XmlTagCloudEntry>() ;
    }
}
