/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class JPAAlbum implements Album, Serializable {
    private static final Logger log = Logger.getLogger(JPAGeolocalisation.class.getName());

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

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public Integer getPicture() {
        return picture;
    }

    @Override
    public void setPicture(Integer picture) {
        this.picture = picture;
    }

    @Override
    public List<Photo> getPhotoList() {
        return (List) jPAPhotoList;
    }

    @Override
    public void setPhotoList(List<Photo> jPAPhotoList) {
        this.jPAPhotoList = (List) jPAPhotoList;
    }

    @Override
    public Utilisateur getDroit() {
        return (Utilisateur) droit;
    }

    @Override
    public void setDroit(Utilisateur droit) {
        this.droit = (JPAUtilisateur) droit;
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
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
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
