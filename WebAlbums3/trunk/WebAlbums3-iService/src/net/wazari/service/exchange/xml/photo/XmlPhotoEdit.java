/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.common.XmlUserList;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
public class XmlPhotoEdit extends XmlInfoException {
    public XmlPhotoSubmit submit;
    public Integer id;
    public String description;
    public XmlUserList userInside;
    public XmlWebAlbumsList tag_used_lst;
    public XmlWebAlbumsList tag_never;
    public XmlWebAlbumsList tag_used;
    public XmlWebAlbumsList tag_nused;
  
}
