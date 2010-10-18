/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.album;

import java.util.LinkedList;
import java.util.List;
import net.wazari.service.exchange.xml.album.XmlAlbum;

/**
 *
 * @author kevin
 */
public class XmlAlbumYear {
    public int year ;

    public XmlAlbumYear() {
        albums = new LinkedList<XmlAlbum>() ;
    }
    public final List<XmlAlbum> albums ;
}
