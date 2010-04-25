/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author kevin
 */
@Entity
@Table(name = "Tag")
@NamedQueries({@NamedQuery(name = "Tag.findAll", query = "SELECT t FROM Tag t"), @NamedQuery(name = "Tag.findById", query = "SELECT t FROM Tag t WHERE t.id = :id"), @NamedQuery(name = "Tag.findByNom", query = "SELECT t FROM Tag t WHERE t.nom = :nom"), @NamedQuery(name = "Tag.findByTagType", query = "SELECT t FROM Tag t WHERE t.tagType = :tagType")})
public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Nom")
    private String nom;
    @Basic(optional = false)
    @Column(name = "TagType")
    private int tagType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag")
    private List<TagTheme> tagThemeList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag")
    private List<TagPhoto> tagPhotoList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "tag1")
    private Geolocalisation geolocalisation;

    public Tag() {
    }

    public Tag(Integer id) {
        this.id = id;
    }

    public Tag(Integer id, String nom, int tagType) {
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
        return tagThemeList;
    }

    public void setTagThemeList(List<TagTheme> tagThemeList) {
        this.tagThemeList = tagThemeList;
    }

    public List<TagPhoto> getTagPhotoList() {
        return tagPhotoList;
    }

    public void setTagPhotoList(List<TagPhoto> tagPhotoList) {
        this.tagPhotoList = tagPhotoList;
    }

    public Geolocalisation getGeolocalisation() {
        return geolocalisation;
    }

    public void setGeolocalisation(Geolocalisation geolocalisation) {
        this.geolocalisation = geolocalisation;
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
        if (!(object instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Tag[id=" + id + "]";
    }

}
