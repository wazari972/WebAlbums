/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.carnet;

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.common.util.StringUtil;
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
    @XmlElementWrapper
    @XmlElement(name="line")
    public List<String> text;
    public String droit;
    public List<XmlPhotoId> photo;
    
    public void setText(String textt) {
        if (textt == null || textt.isEmpty()) {
            return;
        }
        
        textt = StringUtil.escapeXML(textt);
        
        this.text = Arrays.asList(textt.split("\n")) ;
    }
}
