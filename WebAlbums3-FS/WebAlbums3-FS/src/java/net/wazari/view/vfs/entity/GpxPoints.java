/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.view.vfs.entity;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.gpx.GpxType;
import net.wazari.service.exchange.xml.gpx.WptType;
import net.wazari.view.vfs.Launch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevin
 */
@XmlRootElement(name="gpx")
public final class GpxPoints extends GpxType {
    private static final Logger log = LoggerFactory.getLogger(GpxPoints.class.getCanonicalName()) ;
    
    public GpxPoints() {}
    
    public GpxPoints(String name, String lat, String longit) {
        this.addPoint(name, lat, longit);
    }
    
    public void addPoint(String name, String lat, String longit) {
        try {
            WptType new_wpt = new WptType();
            new_wpt.setLat(new BigDecimal(lat));
            new_wpt.setLon(new BigDecimal(longit));
            new_wpt.setName(name);
            this.getWpt().add(new_wpt);
        } catch(NumberFormatException e) {
            log.info("GPX Invalid point: {}, {}/{}", new String[] {name, lat, longit});
        }
    }
    
}
