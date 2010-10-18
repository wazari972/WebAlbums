/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.album;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlUserList;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlAlbumEdit {
    public XmlAlbumSubmit submit;
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
    public XmlWebAlbumsList tag_used;
    public XmlWebAlbumsList tag_nused;
    public XmlWebAlbumsList tag_never;
    public XmlUserList droits;

}
