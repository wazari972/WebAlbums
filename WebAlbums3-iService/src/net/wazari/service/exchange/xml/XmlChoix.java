/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.album.XmlAlbumGraph;
import net.wazari.service.exchange.xml.album.XmlAlbumSelect;
import net.wazari.service.exchange.xml.album.XmlAlbumTop;
import net.wazari.service.exchange.xml.album.XmlAlbumYears;
import net.wazari.service.exchange.xml.carnet.XmlCarnetsTop;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.service.exchange.xml.tag.XmlTagCloud;
import net.wazari.service.exchange.xml.tag.XmlTagPersonsPlaces;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlChoix {
    public XmlTagCloud cloud;
    public XmlTagPersonsPlaces persons;
    public XmlTagPersonsPlaces places;
    public XmlCarnetsTop topCarnets;
    public XmlAlbumTop topAlbums;
    public XmlAlbumYears years;
    public XmlAlbumGraph graph;
    public XmlAlbumSelect select;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used ;
}
