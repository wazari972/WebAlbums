/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.gpx.GpxType;
import net.wazari.service.exchange.xml.gpx.WptType;

/**
 *
 * @author kevin
 */
@XmlRootElement(name="gpx")
public class GpxPoints extends GpxType {

    public GpxPoints() {}
    
    public GpxPoints(String name, String lat, String longit) {
        this.addPoint(name, lat, longit);
    }
    
    public void addPoint(String name, String lat, String longit) {
        WptType new_wpt = new WptType();
        new_wpt.setLat(new BigDecimal(lat));
        new_wpt.setLon(new BigDecimal(longit));
        new_wpt.setName(name);
        this.getWpt().add(new_wpt);
    }
    
}
