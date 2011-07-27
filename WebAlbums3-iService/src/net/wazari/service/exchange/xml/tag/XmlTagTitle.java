/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.tag;

import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */

@XmlRootElement
public class XmlTagTitle {
    public XmlWebAlbumsList tagList;
}
