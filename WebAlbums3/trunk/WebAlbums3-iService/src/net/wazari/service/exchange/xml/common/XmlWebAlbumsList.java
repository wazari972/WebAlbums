/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.common;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Mode;
import net.wazari.service.exchange.xml.tag.XmlTag;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlWebAlbumsList extends XmlInfoException {
    @XmlAttribute
    public Mode mode;

    public String blob;

    @XmlAttribute
    public Box box;
    @XmlAttribute
    public Integer id;
    @XmlAttribute
    public String type ;

    public void addTag(XmlTag newTag) {
        if (newTag == null) return ;

        if (newTag instanceof XmlWebAlbumsTagWho) {
            who.add((XmlWebAlbumsTagWho) newTag);
        } else if (newTag instanceof XmlWebAlbumsTagWhat) {
            what.add((XmlWebAlbumsTagWhat) newTag);
        } else if (newTag instanceof XmlWebAlbumsTagWhere) {
            where.add((XmlWebAlbumsTagWhere) newTag);
        } else {
            tag.add(newTag);
        }
    }

    public static class XmlWebAlbumsTagWhere extends XmlTag {
        public String getNature() {return "where" ;}
    }
    public static class XmlWebAlbumsTagWhat extends XmlTag {
        public String getNature() {return "what" ;}
    }
    public static class XmlWebAlbumsTagWho extends XmlTag {
        public String getNature() {return "who" ;}
    }

    public List<XmlTag> tag = new LinkedList<XmlTag>() ;
    public List<XmlWebAlbumsTagWho> who = new LinkedList<XmlWebAlbumsTagWho>() ;
    public List<XmlWebAlbumsTagWhat> what = new LinkedList<XmlWebAlbumsTagWhat>() ;
    public List<XmlWebAlbumsTagWhere> where = new LinkedList<XmlWebAlbumsTagWhere>() ;
}