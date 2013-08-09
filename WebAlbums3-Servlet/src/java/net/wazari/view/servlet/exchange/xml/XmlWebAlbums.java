/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.wazari.service.exchange.xml.*;
import net.wazari.service.exchange.xml.common.XmlLoginInfo;
import net.wazari.service.exchange.xml.config.XmlConfig;

/**
 *
 * @author kevin
 */
@XmlRootElement(name= "webAlbums")
public class XmlWebAlbums {
    @XmlTransient
    public boolean isComplete ;
    @XmlTransient
    public boolean isBlob ;
    @XmlTransient
    public String blob;
    @XmlTransient
    public String xslFile;

    /***/
    public XmlLogin login;
    public XmlThemes themes;
    public XmlMaint maint;
    public XmlChoix choix;
    public XmlAlbums albums;
    public XmlCarnets carnets;
    public XmlPhotos photos;
    public XmlConfig config;
    public XmlTags tags;
    public XmlImage image;
    public XmlDatabase database;
    /***/
    public XmlAffichage affichage;
    public XmlLoginInfo loginInfo;
    public String time ;
}
