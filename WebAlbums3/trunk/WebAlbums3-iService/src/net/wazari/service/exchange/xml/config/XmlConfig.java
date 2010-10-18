/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;
import net.wazari.service.exchange.xml.config.XmlConfigDelTag;
import net.wazari.service.exchange.xml.config.XmlConfigDelTheme;
import net.wazari.service.exchange.xml.config.XmlConfigImport;
import net.wazari.service.exchange.xml.config.XmlConfigLinkTag;
import net.wazari.service.exchange.xml.config.XmlConfigModGeo;
import net.wazari.service.exchange.xml.config.XmlConfigModTag;
import net.wazari.service.exchange.xml.config.XmlConfigModVis;
import net.wazari.service.exchange.xml.config.XmlConfigNewTag;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlConfig extends XmlInfoException {
    public XmlConfigImport irnport;
    public XmlConfigNewTag newtag;
    public XmlConfigModTag modtag;
    public XmlConfigModVis modvis;
    public XmlConfigModGeo modgeo;
    public XmlConfigLinkTag linktag;
    public XmlConfigDelTag deltag;
    public XmlConfigDelTheme deltheme;
    public String shutdown;

    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_used;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_geo;
    @XmlElement(name = "tagList")
    public XmlWebAlbumsList tag_never;

}
