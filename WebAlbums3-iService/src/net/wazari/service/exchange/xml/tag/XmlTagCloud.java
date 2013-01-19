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
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kevin
 */
public class XmlTagCloud {
    @XmlElement(name = "tag")
    public List<XmlTagCloudEntry> parentList = new LinkedList<XmlTagCloudEntry>();

    public static class XmlTagCloudEntry {
        public String name;
        @XmlAttribute
        public Integer id;
    
        @XmlAttribute
        public String type = null;
        @XmlAttribute
        public int size;
        @XmlAttribute
        public Long nb;
        
        @XmlElementWrapper(name="children")
        @XmlElement(name="tag")
        public List<XmlTagCloudEntry> children;
        
        @XmlTransient
        public XmlTag.GeoLoc loc = null ;
        
        public void setGeo(String latitude, String longitude) {
        if (loc == null) {
            loc = new XmlTag.GeoLoc();
        }
        
        loc.lat = latitude;
        loc.longit = longitude;
    }
    }
}
