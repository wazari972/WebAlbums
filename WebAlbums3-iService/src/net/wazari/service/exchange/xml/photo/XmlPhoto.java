/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPhoto extends XmlInfoException {
    public XmlPhotoSubmit submit;
    @XmlAttribute
    public boolean checked;
    public XmlDetails details;
    @XmlAttribute
    public int count;
    public XmlPhotoExif exif;
}
