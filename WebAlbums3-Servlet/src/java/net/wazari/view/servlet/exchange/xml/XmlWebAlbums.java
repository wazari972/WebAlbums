/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.wazari.service.exchange.xml.XmlAffichage;
import net.wazari.service.exchange.xml.XmlChoix;
import net.wazari.service.exchange.xml.carnet.XmlCarnet;
import net.wazari.service.exchange.xml.config.XmlConfig;
import net.wazari.service.exchange.xml.XmlImage;
import net.wazari.service.exchange.xml.XmlLogin;
import net.wazari.service.exchange.xml.XmlMaint;
import net.wazari.service.exchange.xml.XmlThemes;
import net.wazari.service.exchange.xml.common.XmlLoginInfo;

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
    public XmlPhotos photos;
    public XmlConfig config;
    public XmlTags tags;
    public XmlImage image;
    public XmlDatabase database;
    /***/
    public XmlAffichage affichage;
    public XmlLoginInfo loginInfo;
    public String time ;
    public XmlCarnet carnet;
}
