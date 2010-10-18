/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.photo;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlPhotoExif {
    public List<XmlPhotoExifEntry> entries = new LinkedList<XmlPhotoExifEntry>() ;
    public static class XmlPhotoExifEntry {

        public XmlPhotoExifEntry() {}
        public XmlPhotoExifEntry(String name, String value) {
            this.name = name;
            this.value = value;
        }
        
        public String name ;
        public String value ;
    }
}
