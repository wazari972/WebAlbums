/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPhotoMassEdit extends XmlInfoException {
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_never;

}
