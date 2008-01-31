/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wazari.service.exchange.xml.photo;

import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author kevin
 */
public class XmlPhotoFastEdit {
    
    public enum Status {OK, ERROR}
    @XmlAttribute
    public Status desc_status;
    public String desc_msg;
    @XmlAttribute
    public Status tag_status;
    public String tag_msg;
    @XmlAttribute
    public Status stars_status;
    public String stars_msg;
}
