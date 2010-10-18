/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.view.servlet.exchange.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.wazari.service.exchange.xml.XmlChoix;
import net.wazari.service.exchange.xml.XmlConfig;
import net.wazari.service.exchange.xml.XmlImage;
import net.wazari.service.exchange.xml.XmlLogin;
import net.wazari.service.exchange.xml.XmlMaint;
import net.wazari.service.exchange.xml.XmlVoid;

/**
 *
 * @author kevin
 */
@XmlRootElement(name= "webAlbums")
public class XmlWebAlbums {
    @XmlTransient
    public boolean isComplete ;
    @XmlTransient
    public String xslFile;

    /***/
    
    @XmlElement
    public XmlLogin login;

    @XmlElement
    public XmlVoid woid;

    @XmlElement
    public XmlMaint maint;

    @XmlElement
    public XmlChoix choix;

    @XmlTransient
    public String blob;

    @XmlElement
    public XmlAlbums album;

    @XmlElement
    public XmlPhotos photo;

    @XmlElement
    public XmlConfig config;

    @XmlElement
    public XmlTag tag;

    @XmlElement
    public XmlImage image;

    /***/

    @XmlElement
    public String affichage;

    @XmlElement
    public String loginInfo;

    @XmlElement
    public String time ;

}
