/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlInfoException;
import net.wazari.service.exchange.xml.common.XmlWebAlbumsList;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlConfig extends XmlInfoException {
    public XmlConfigImport irnport;
    public XmlConfigNewTag newtag;
    public XmlConfigSetHome sethome;
    public XmlConfigModTag modtag;
    public XmlConfigModVis modvis;
    public XmlConfigModGeo modgeo;
    public XmlConfigModPers modpers;
    public XmlConfigModMinor modminor;
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
