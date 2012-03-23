/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.carnet;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlDate;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.service.exchange.xml.photo.XmlPhotoId;
/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlCarnet {
    @XmlAttribute
    public Integer id;
    public XmlPhotoId picture;
    public XmlDate date;
    @XmlAttribute
    public Integer carnetsPage;
    public String name;
    public XmlDetails details;
    public String text;
    public String droit;
    public List<XmlPhotoId> photo;
}
