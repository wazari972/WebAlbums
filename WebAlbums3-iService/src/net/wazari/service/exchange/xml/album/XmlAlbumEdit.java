/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.album;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.XmlThemeList;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlAlbumEdit {
    public XmlAlbumSubmit submit;
    public String exception;
    
    public XmlAlbum album = new XmlAlbum();
    
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_nused;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_never;
    public XmlThemeList themes;
}
