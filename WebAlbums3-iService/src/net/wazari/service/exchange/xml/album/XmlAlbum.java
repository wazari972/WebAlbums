/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.album;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
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
    @XmlAttribute
    public Integer count;
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
    
    public List<XmlCarnet> carnet;
}
