/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.XmlPage;
import net.wazari.service.exchange.xml.album.XmlAlbum;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPhotoDisplay extends XmlInfoException{
    public XmlAlbum album;
    public XmlPage page;
    public XmlPhotoList photoList;
}
