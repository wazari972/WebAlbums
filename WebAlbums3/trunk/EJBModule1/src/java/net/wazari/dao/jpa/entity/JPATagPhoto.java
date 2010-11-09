/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "TagPhoto",
    uniqueConstraints = {@UniqueConstraint(columnNames={"Tag", "Photo"})}
)
public class JPATagPhoto implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPATagPhoto.class.getName());

    private static final long serialVersionUID = 1L;

    @XmlTransient
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Long id;

    @XmlTransient
    @JoinColumn(name = "Tag", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPATag tag;

    @XmlTransient
    @JoinColumn(name = "Photo", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAPhoto photo;

    @XmlTransient
    @Transient
    private Integer tagId ;

    @XmlTransient
    @Transient
    private Integer photoId ;

    public JPATagPhoto() {
    }

    public JPATagPhoto(Long id) {
        this.id = id;
    }

    
    public JPATag getTag() {
        return tag;
    }

    
    public void setTag(JPATag tag) {
        this.tag = (JPATag) tag;
    }

    
    public JPAPhoto getPhoto() {
        return photo;
    }

    
    public void setPhoto(JPAPhoto photo) {
        this.photo = (JPAPhoto) photo;
        
    }

    @XmlAttribute
    public Integer getPhotoId() {
        if (photo == null) {
            return photoId ;
        } else {
            return photo.getId() ;
        }
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId ;
    }

    @XmlAttribute
    public Integer getTagId() {
        if (tag == null) {
            return tagId ;
        } else {
            return tag.getId() ;
        }
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId ;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
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

    
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPATagPhoto[Tag=" + (getTag() == null ? "null" : getTag().getId()) + ", Photo="+(getPhoto() == null ? "null" : getPhoto().getId())+"]";
    }

}
