/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.logging.Logger;
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
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagTheme;
import net.wazari.dao.entity.Theme;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "TagTheme")
public class JPATagTheme implements TagTheme, Serializable {
    private static final Logger log = Logger.getLogger(JPATagTheme.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "Photo")
    private Integer photo;

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
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getPhoto() {
        return photo;
    }

    @Override
    public void setPhoto(Integer photo) {
        this.photo = photo;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
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
        return "net.wazari.dao.jpa.entity.JPATagTheme[id=" + id + "]";
    }

}
