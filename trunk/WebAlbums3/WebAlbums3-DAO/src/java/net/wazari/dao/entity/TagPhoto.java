/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
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
@Table(name = "TagPhoto")
@NamedQueries({@NamedQuery(name = "TagPhoto.findAll", query = "SELECT t FROM TagPhoto t"), @NamedQuery(name = "TagPhoto.findById", query = "SELECT t FROM TagPhoto t WHERE t.id = :id")})
public class TagPhoto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @JoinColumn(name = "Tag", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Tag tag;
    @JoinColumn(name = "Photo", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Photo photo;

    public TagPhoto() {
    }

    public TagPhoto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
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
        if (!(object instanceof TagPhoto)) {
            return false;
        }
        TagPhoto other = (TagPhoto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TagPhoto[id=" + id + "]";
    }

}
