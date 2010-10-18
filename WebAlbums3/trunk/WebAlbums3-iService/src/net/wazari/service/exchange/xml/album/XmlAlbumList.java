/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.album;

import java.util.ArrayList;
import java.util.List;
import net.wazari.service.exchange.xml.XmlPage;

/**
 *
 * @author kevin
 */
public class XmlAlbumList {
    public XmlAlbumList(int length) {
        albums = new ArrayList<XmlAlbum>(length) ;
    }
    public final List<XmlAlbum> albums ;
    public XmlPage page;
}
