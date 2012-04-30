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
import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.entity.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "TagTheme",
    uniqueConstraints = {@UniqueConstraint(columnNames={"Tag", "Theme"})}
)
public class JPATagTheme implements TagTheme, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPATagTheme.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @JoinColumn(name = "Photo", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private JPAPhoto photo;
    
    @Column(name = "isVisible")
    private Boolean isVisible;

    @JoinColumn(name = "Theme", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPATheme theme;

    @JoinColumn(name = "Tag", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPATag tag;

    public JPATagTheme() {
    }

    public JPATagTheme(Integer id) {
        this.id = id;
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
        if (this.photo == null) {
            return null;
        } else {
            return this.photo.getId();
        }
    }
    
    @Transient
    public Integer photoId;
    private void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    @XmlAttribute
    public Boolean isVisible() {
        if (isVisible == null || isVisible)
            return null;
        else
            return false;
    }
    
    @Override
    public Boolean getIsVisible() {
        return isVisible;
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    @Override
    public Theme getTheme() {
        return (Theme) theme;
    }

    @Override
    public void setTheme(Theme theme) {
        this.theme = (JPATheme) theme;
    }
    
    @Override
    public Tag getTag() {
        return tag;
    }

    @Override
    public void setTag(Tag tag) {
        this.tag = (JPATag) tag;
    }

    @XmlAttribute
    public Integer getTagId() {
        return tag.getId() ;
    }

    @Transient
    public Integer tagId;
    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JPATagTheme)) {
            return false;
        }
        JPATagTheme other = (JPATagTheme) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPATagTheme[Tag=" + (getTag() == null ? "null" : getTag().getId()) + ", Theme="+(getTheme() == null ? "null" : getTheme().getId())+"]";
    }

}
