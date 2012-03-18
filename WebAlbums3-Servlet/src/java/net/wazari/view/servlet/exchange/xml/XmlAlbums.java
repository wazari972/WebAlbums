/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import net.wazari.service.exchange.xml.album.XmlAlbumAbout;
import net.wazari.service.exchange.xml.album.XmlAlbumDisplay;
import net.wazari.service.exchange.xml.album.XmlAlbumEdit;
import net.wazari.service.exchange.xml.album.XmlAlbumGraph;
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
    public XmlAlbumGraph graph;
    public XmlAlbumSelect select;
    public XmlAlbumEdit edit;
    public XmlAlbumDisplay display;
    public XmlAlbumAbout about;
    public XmlAlbums(){}
}
