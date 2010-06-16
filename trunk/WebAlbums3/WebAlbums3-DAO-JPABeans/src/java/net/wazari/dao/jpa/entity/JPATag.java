/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import net.wazari.dao.entity.Geolocalisation;
import net.wazari.dao.entity.Tag;
import net.wazari.dao.entity.TagPhoto;
import net.wazari.dao.entity.TagTheme;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Tag")
@NamedQueries({@NamedQuery(name = "JPATag.findAll", query = "SELECT j FROM JPATag j")})
public class JPATag implements Tag, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Nom", nullable = false, length = 100)
    private String nom;
    @Basic(optional = false)
    @Column(name = "TagType", nullable = false)
    private int tagType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag", fetch = FetchType.LAZY)
    private List<JPATagTheme> jPATagThemeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag", fetch = FetchType.LAZY)
    private List<JPATagPhoto> jPATagPhotoList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "jPATag", fetch = FetchType.LAZY)
    private JPAGeolocalisation jPAGeolocalisation;

    public JPATag() {
    }

    public JPATag(Integer id) {
        this.id = id;
    }

    public JPATag(Integer id, String nom, int tagType) {
        this.id = id;
        this.nom = nom;
        this.tagType = tagType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public List<TagTheme> getTagThemeList() {
        return (List) jPATagThemeList;
    }

    public void setTagThemeList(List<TagTheme> jPATagThemeList) {
        this.jPATagThemeList = (List) jPATagThemeList;
    }

    public List<TagPhoto> getTagPhotoList() {
        return (List) jPATagPhotoList;
    }

    public void setTagPhotoList(List<TagPhoto> jPATagPhotoList) {
        this.jPATagPhotoList = (List) jPATagPhotoList;
    }

    public JPAGeolocalisation getGeolocalisation() {
        return jPAGeolocalisation;
    }

    public void setGeolocalisation(Geolocalisation jPAGeolocalisation) {
        this.jPAGeolocalisation = (JPAGeolocalisation) jPAGeolocalisation;
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
        if (!(object instanceof JPATag)) {
            return false;
        }
        JPATag other = (JPATag) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPATag[id=" + id + "]";
    }

}
