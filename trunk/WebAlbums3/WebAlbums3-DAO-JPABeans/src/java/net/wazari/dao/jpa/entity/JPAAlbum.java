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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Album")
@NamedQueries({@NamedQuery(name = "JPAAlbum.findAll", query = "SELECT j FROM JPAAlbum j")})
public class JPAAlbum implements Album, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Nom", nullable = false, length = 100)
    private String nom;
    @Column(name = "Description", length = 255)
    private String description;
    @Basic(optional = false)
    @Column(name = "Date", nullable = false, length = 10)
    private String date;

    @JoinColumn(name = "Picture", nullable = true)
    private Integer picture;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "album", fetch = FetchType.LAZY)
    private List<JPAPhoto> jPAPhotoList;

    @JoinColumn(name = "Droit", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAUtilisateur droit;
    
    @JoinColumn(name = "Theme", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPATheme theme;

    public JPAAlbum() {
    }

    public JPAAlbum(Integer id) {
        this.id = id;
    }

    public JPAAlbum(Integer id, String nom, String date) {
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
        return (List) jPAPhotoList;
    }

    public void setPhotoList(List<Photo> jPAPhotoList) {
        this.jPAPhotoList = (List) jPAPhotoList;
    }

    public Utilisateur getDroit() {
        return (Utilisateur) droit;
    }

    public void setDroit(Utilisateur droit) {
        this.droit = (JPAUtilisateur) droit;
    }

    public Theme getTheme() {
        return (Theme) theme;
    }

    public void setTheme(Theme theme) {
        this.theme = (JPATheme) theme;
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
        if (!(object instanceof JPAAlbum)) {
            return false;
        }
        JPAAlbum other = (JPAAlbum) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPAAlbum[id=" + id + "]";
    }

}
