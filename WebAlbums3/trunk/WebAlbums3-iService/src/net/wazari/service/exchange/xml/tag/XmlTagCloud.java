/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.tag;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author kevin
 */
public class XmlTagCloud {
    public XmlTagCloudEntry parentTag;
    public static class XmlTagCloudEntry {
        @XmlAttribute
        public int size;
        @XmlAttribute
        public Long nb;
        @XmlAttribute
        public Integer id;
        public String name;
        public List<XmlTagCloudEntry> sonList = new LinkedList<XmlTagCloudEntry>() ;
    }
}
