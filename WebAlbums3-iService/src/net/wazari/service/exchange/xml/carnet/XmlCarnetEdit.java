/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.carnet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlUserList;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlCarnetEdit {
    public XmlCarnetSubmit submit;
    public String exception;
    @XmlAttribute
    public Integer picture;
    public String name;
    @XmlAttribute
    public Integer count;
    @XmlAttribute
    public Integer id;
    public String description;
    public String date;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_nused;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_never;
    public XmlUserList rights;

}
