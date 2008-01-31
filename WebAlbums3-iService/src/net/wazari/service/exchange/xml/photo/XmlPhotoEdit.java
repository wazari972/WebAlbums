/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.common.XmlUserList;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPhotoEdit extends XmlInfoException {
    public XmlPhotoSubmit submit;
    @XmlAttribute
    public Integer id;
    public String description;
    public XmlUserList rights;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used_lst;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_never;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_nused;
    @XmlAttribute
    public Integer album;
  
}
