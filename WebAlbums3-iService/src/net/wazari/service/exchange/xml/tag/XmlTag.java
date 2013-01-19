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
import net.wazari.service.exchange.xml.photo.XmlPhotoId;

/**
 *
 * @author kevin
 */
public class XmlTag {
    public String name;
    @XmlAttribute
    public Integer id;
    public XmlPhotoId picture;
    @XmlAttribute
    public Boolean checked;
    @XmlAttribute
    public Boolean minor;
    
    @XmlElementWrapper(name="children")
    @XmlElement(name="tag")
    public List<XmlTag> children;
    
    public void addChildren(XmlTag tag) {
        if (children == null) {
            children = new LinkedList<XmlTag>();
        }
        children.add(tag);
    }
    
    @XmlTransient
    public GeoLoc loc = null ;
    
    public static class GeoLoc{
        public String lat;
        public String longit;
    }
    
    public void setGeo(String latitude, String longitude) {
        if (loc == null) {
            loc = new GeoLoc();
        }
        
        loc.lat = latitude;
        loc.longit = longitude;
    }
}
