/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.album;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import net.wazari.service.exchange.xml.carnet.XmlCarnet;
import net.wazari.service.exchange.xml.common.XmlDate;
import net.wazari.service.exchange.xml.common.XmlDetails;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlAlbum {
    @XmlAttribute
    public Integer id;
    public String title;
    public XmlAlbumSubmit submit;
    public XmlDetails details;
    public String name;
    @XmlAttribute
    public Integer picture;
    public XmlDate date;
    @XmlAttribute
    public Long time;
    public String droit;
    public String albmDate;
    public List<XmlGpx> gpx;
    public List<XmlCarnet> carnet;
    @XmlRootElement
    public static class Counter {
        public Counter(int count) {
            this.value = count;
        }
        public Counter() {
            this.value = 0;
        }
        @XmlValue
        public Integer value;
        public void inc() {
            this.value++ ;
        }
    }
    
    public Map<String, Counter> photoCount = new HashMap<String, Counter>();
}
