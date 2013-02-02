/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange.xml.album;

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlGpx {
    @XmlAttribute
    public Integer id;
    @XmlAttribute
    public Integer albumId;
    @XmlElement
    public String albumName;
    @XmlElementWrapper
    @XmlElement(name="line")
    public List<String> description;
    @XmlElement
    public String path;
    
    public void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            return;
        }
        
        this.description = Arrays.asList(description.split("\n")) ;
    }
}
