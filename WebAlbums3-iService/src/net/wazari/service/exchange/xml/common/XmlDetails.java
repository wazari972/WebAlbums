/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.common;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.photo.XmlPhotoId;

/**
 *
 * @author kevin
 */

@XmlRootElement
public class XmlDetails {
    @XmlAttribute
    public Boolean isGpx;
    public XmlPhotoId photoId;
    public List<String> userInside;
    public String description;
    @XmlAttribute
    public Integer albumId;
    public String albumName;
    public String albumDate;
    public XmlPhotoAlbumUser user;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used;
    @XmlAttribute
    public Integer stars;
}
