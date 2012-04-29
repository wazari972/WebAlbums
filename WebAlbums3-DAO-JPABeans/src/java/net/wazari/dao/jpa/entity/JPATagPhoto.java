/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "TagPhoto",
    uniqueConstraints = {@UniqueConstraint(columnNames={"Tag", "Photo"})}
)
public class JPATagPhoto implements TagPhoto, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPATagPhoto.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Long id;

    @JoinColumn(name = "Tag", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPATag tag;

    @JoinColumn(name = "Photo", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAPhoto photo;

    public JPATagPhoto() {
    }

    public JPATagPhoto(Long id) {
        this.id = id;
    }

    @Override
    public Tag getTag() {
        return tag;
    }

    @Override
    public void setTag(Tag tag) {
        this.tag = (JPATag) tag;
    }

    @Override
    public Photo getPhoto() {
        return photo;
    }

    @Override
    public void setPhoto(Photo photo) {
        this.photo = (JPAPhoto) photo;
        
    }

    @XmlAttribute
    public Integer getPhotoId() {
        return photo.getId() ;
    }

    @XmlAttribute
    public Integer getTagId() {
        return tag.getId() ;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JPATagPhoto)) {
            return false;
        }
        JPATagPhoto other = (JPATagPhoto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPATagPhoto[Tag=" + (getTag() == null ? "null" : getTag().getId()) + ", Photo="+(getPhoto() == null ? "null" : getPhoto().getId())+"]";
    }

}
