/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlChoix {

    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used ;
}
