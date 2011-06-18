/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.carnet;

import net.wazari.service.exchange.xml.album.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.XmlPage;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlCarnetList {
    public XmlCarnetList() {
        album = new LinkedList<XmlAlbum>() ;
    }
    public XmlCarnetList(int length) {
        album = new ArrayList<XmlAlbum>(length) ;
    }
    public final List<XmlAlbum> album ;
    public XmlPage page;
}
