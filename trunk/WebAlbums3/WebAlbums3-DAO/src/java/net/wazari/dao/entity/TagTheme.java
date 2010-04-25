/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author kevin
 */
@Entity
@Table(name = "TagTheme")
@NamedQueries({@NamedQuery(name = "TagTheme.findAll", query = "SELECT t FROM TagTheme t"), @NamedQuery(name = "TagTheme.findById", query = "SELECT t FROM TagTheme t WHERE t.id = :id"), @NamedQuery(name = "TagTheme.findByPhoto", query = "SELECT t FROM TagTheme t WHERE t.photo = :photo"), @NamedQuery(name = "TagTheme.findByIsVisible", query = "SELECT t FROM TagTheme t WHERE t.isVisible = :isVisible")})
public class TagTheme implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Photo")
    private Integer photo;
    @Column(name = "isVisible")
    private Boolean isVisible;
    @JoinColumn(name = "Theme", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Theme theme;
    @JoinColumn(name = "Tag", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Tag tag;

    public TagTheme() {
    }

    public TagTheme(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPhoto() {
        return photo;
    }

    public void setPhoto(Integer photo) {
        this.photo = photo;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
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
        if (!(object instanceof TagTheme)) {
            return false;
        }
        TagTheme other = (TagTheme) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TagTheme[id=" + id + "]";
    }

}
