/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import net.wazari.service.exchange.xml.common.XmlDetails;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
public class XmlPhoto extends XmlInfoException {
    public XmlPhotoSubmit submit;
    public boolean checked;
    public XmlDetails details;
    public int count;
    public XmlPhotoExif exifs;

}
