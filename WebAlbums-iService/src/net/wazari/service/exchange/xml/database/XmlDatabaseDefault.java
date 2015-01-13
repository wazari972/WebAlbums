/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange.xml.database;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlDatabaseDefault extends XmlInfoException {
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used;
}
