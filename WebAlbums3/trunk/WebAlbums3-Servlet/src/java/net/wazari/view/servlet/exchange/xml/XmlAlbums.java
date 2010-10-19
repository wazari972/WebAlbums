/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.album.XmlAlbumDisplay;
import net.wazari.service.exchange.xml.album.XmlAlbumEdit;
import net.wazari.service.exchange.xml.album.XmlAlbumSelect;
import net.wazari.service.exchange.xml.album.XmlAlbumTop;
import net.wazari.service.exchange.xml.album.XmlAlbumYears;

/**
 *
 * @author kevin
 */
public class XmlAlbums {
    public XmlAlbumTop top;
    public XmlAlbumYears years;
    public XmlAlbumSelect select;
    public XmlAlbumEdit edit;
    public XmlAlbumDisplay display;

}
