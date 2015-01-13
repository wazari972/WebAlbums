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
            children = new LinkedList<>();
        }
        children.add(tag);
    }
}
