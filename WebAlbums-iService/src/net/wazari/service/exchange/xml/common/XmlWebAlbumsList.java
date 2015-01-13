/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.service.exchange.xml.common;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.service.exchange.ViewSession.Box;
import net.wazari.service.exchange.ViewSession.Tag_Mode;
import net.wazari.service.exchange.xml.tag.XmlTag;

/**
 *
 * @author kevin
 */
@XmlRootElement
public class XmlWebAlbumsList extends XmlInfoException {
    public enum ListType {UNKNOWN, PHOTO, ALBUM, CARNET} ;
    
    @XmlAttribute
    public Tag_Mode mode;

    public String blob;

    @XmlAttribute
    public Box box;
    @XmlAttribute
    public Integer id;
    @XmlAttribute
    public ListType type ;

    public List<XmlTag> tag = new LinkedList<>() ;
    public List<XmlWebAlbumsTagWho> who = new LinkedList<>() ;
    public List<XmlWebAlbumsTagWhat> what = new LinkedList<>() ;
    public List<XmlWebAlbumsTagWhere> where = new LinkedList<>() ;
    
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
        @XmlAttribute
        public String lat;
        @XmlAttribute
        public String lng;
        public String getNature() {return "where" ;}
        public void setGeo(String latitude, String longitude) {        
            lat = latitude;
            lng = longitude;
        }
    }
    public static class XmlWebAlbumsTagWhat extends XmlTag {
        public String getNature() {return "what" ;}
    }
    public static class XmlWebAlbumsTagWho extends XmlTag {
        public String birthdate;
        public String contact;
        public String getNature() {return "who" ;}
    }
    
    public List<XmlTag> getAllTags() {
        List<XmlTag> alltags = new LinkedList<>();
        alltags.addAll(this.tag);
        alltags.addAll(this.who);
        alltags.addAll(this.what);
        alltags.addAll(this.where);
        
        return alltags;
    }
}
