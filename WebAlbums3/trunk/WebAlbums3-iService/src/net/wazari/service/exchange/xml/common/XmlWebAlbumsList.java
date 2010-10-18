/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.common;

import java.util.LinkedList;
import java.util.List;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.xml.tag.XmlTag;

/**
 *
 * @author kevin
 */
public class XmlWebAlbumsList extends XmlInfoException {
    public Mode mode;
    public String text;
    public Box box;
    public String type;
    public Integer id;

    public static class XmlWebAlbumsListTags extends XmlWebAlbumsList {}

    public static class XmlWebAlbumsTagWhere extends XmlTag {} ;
    public static class XmlWebAlbumsTagWhat extends XmlTag {} ;
    public static class XmlWebAlbumsTagWho extends XmlTag {} ;

    public List<XmlTag> tags = new LinkedList<XmlTag>() ;

}
