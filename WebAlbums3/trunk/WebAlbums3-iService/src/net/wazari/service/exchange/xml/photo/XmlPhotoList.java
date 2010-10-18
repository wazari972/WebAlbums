/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.xml.XmlPage;
import net.wazari.service.exchange.xml.common.XmlInfoException;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPhotoList extends XmlInfoException {
    public final List<XmlPhoto> photo ;
    public XmlPhotoSubmit submit;
    public XmlPhotoMassEdit massEdit;
    public XmlPage page;

    public XmlPhotoList() {
        photo = new LinkedList<XmlPhoto>() ;
    }
    public XmlPhotoList(int size) {
        photo = new ArrayList<XmlPhoto>(size) ;
    }

}
