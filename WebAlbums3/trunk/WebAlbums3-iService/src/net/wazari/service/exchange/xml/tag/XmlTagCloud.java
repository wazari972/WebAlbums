/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.tag;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author kevin
 */
public class XmlTagCloud {
    public XmlTagCloudEntry parentTag;
    public static class XmlTagCloudEntry {
        public int size;
        public Long nb;
        public Integer id;
        public String name;
        public List<XmlTagCloudEntry> sonList = new LinkedList<XmlTagCloudEntry>() ;
    }
}
