/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import java.util.ArrayList;
import java.util.List;
import net.wazari.service.exchange.xml.XmlPage;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
public class XmlPhotoList extends XmlInfoException {
    public final List<XmlPhoto> photos ;
    public XmlPhotoSubmit submit;
    public XmlPhotoMassEdit massEdit;
    public XmlPage page;
    public XmlPhotoList(int size) {
        photos = new ArrayList<XmlPhoto>(size) ;
    }

}
