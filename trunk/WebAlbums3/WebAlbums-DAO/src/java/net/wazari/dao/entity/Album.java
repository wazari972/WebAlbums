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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author kevin
 */
@Entity
@Table(name = "Album")
@NamedQueries({@NamedQuery(name = "Album.findAll", query = "SELECT a FROM Album a"), @NamedQuery(name = "Album.findById", query = "SELECT a FROM Album a WHERE a.id = :id"), @NamedQuery(name = "Album.findByNom", query = "SELECT a FROM Album a WHERE a.nom = :nom"), @NamedQuery(name = "Album.findByDescription", query = "SELECT a FROM Album a WHERE a.description = :description"), @NamedQuery(name = "Album.findByDate", query = "SELECT a FROM Album a WHERE a.date = :date"), @NamedQuery(name = "Album.findByPicture", query = "SELECT a FROM Album a WHERE a.picture = :picture")})
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Nom")
    private String nom;
    @Column(name = "Description")
    private String description;
    @Basic(optional = false)
    @Column(name = "Date")
    private String date;
    @Column(name = "Picture")
    private Integer picture;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "album")
    private List<Photo> photoList;
    @JoinColumn(name = "Droit", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Utilisateur droit;
    @JoinColumn(name = "Theme", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Theme theme;

    public Album() {
    }

    public Album(Integer id) {
        this.id = id;
    }

    public Album(Integer id, String nom, String date) {
        this.id = id;
        this.nom = nom;
        this.date = date;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getPicture() {
        return picture;
    }

    public void setPicture(Integer picture) {
        this.picture = picture;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public Utilisateur getDroit() {
        return droit;
    }

    public void setDroit(Utilisateur droit) {
        this.droit = droit;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
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
        if (!(object instanceof Album)) {
            return false;
        }
        Album other = (Album) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Album[id=" + id + "]";
    }

}
