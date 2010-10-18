/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.album;

import net.wazari.service.exchange.xml.common.XmlUserList;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
public class XmlAlbumEdit {
    public XmlAlbumSubmit submit;
    public String exception;
    public Integer picture;
    public String name;
    public Integer count;
    public Integer id;
    public String description;
    public String date;
    public XmlWebAlbumsList tag_used;
    public XmlWebAlbumsList tag_nused;
    public XmlWebAlbumsList tag_never;
    public XmlUserList droits;

}
