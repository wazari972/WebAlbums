/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import javax.xml.bind.annotation.XmlElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
public class XmlBenchmark extends XmlInfoException {
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used;
}
