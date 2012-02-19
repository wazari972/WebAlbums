/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlAffichage {
    @XmlAttribute
    public Boolean background;
    @XmlAttribute
    public Boolean remote;
    @XmlAttribute
    public Integer photoAlbumSize;

}
