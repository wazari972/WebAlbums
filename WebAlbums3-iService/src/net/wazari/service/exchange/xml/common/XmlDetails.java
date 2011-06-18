/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.common;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlDetails {
    public Integer photoId;
    public List<String> userInside;
    public String description;
    public Integer albumId;
    public String user;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used;

}
