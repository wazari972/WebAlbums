/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.tag;

import javax.xml.bind.annotation.XmlAttribute;
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
