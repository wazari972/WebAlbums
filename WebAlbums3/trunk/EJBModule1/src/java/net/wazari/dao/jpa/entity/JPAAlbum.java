/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Album")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JPAAlbum implements  Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAGeolocalisation.class.getName());

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    @Id
    @Basic(optional = false)
    private Integer id;

    @XmlElement
    @Basic(optional = false)
    @Column(name = "Nom", nullable = false, length = 100)
    private String nom;

    @XmlElement
    @Column(name = "Description", length = 255)
    private String description;

    @XmlAttribute
    @Basic(optional = false)
    @Column(name = "AlbumDate", nullable = false, length = 10)
    private String date;

    @XmlAttribute
    @Column(name = "Picture", nullable = true)
    private Integer picture;

    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "album", fetch = FetchType.LAZY)
    private List<JPAPhoto> jPAPhotoList;

    @XmlTransient
    @JoinColumn(name = "Droit", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAUtilisateur droit;
    
    @XmlTransient
    @JoinColumn(name = "Theme", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPATheme theme;

    @XmlTransient
    @Transient
    private Integer themeId = null ;

    @XmlTransient
    @Transient
    private Integer droitId = null ;

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

    
    public List<JPAPhoto> getPhotoList() {
        return (List) jPAPhotoList;
    }

    
    public void setPhotoList(List<JPAPhoto> jPAPhotoList) {
        this.jPAPhotoList = (List) jPAPhotoList;
    }

    
    public JPAUtilisateur getDroit() {
        return (JPAUtilisateur) droit;
    }

    
    public void setDroit(JPAUtilisateur droit) {
        this.droit = (JPAUtilisateur) droit;
    }

    
    public JPATheme getTheme() {
        return (JPATheme) theme;
    }

    
    public void setTheme(JPATheme theme) {
        this.theme = (JPATheme) theme;
    }

    @XmlAttribute
    public Integer getDroitId() {
        if (droit == null) {
            return droitId ;
        } else {
            return droit.getId() ;
        }
    }

    public void setDroitId(Integer droitId) {
        this.droitId = droitId ;
    }

    @XmlAttribute
    public Integer getThemeId() {
        if (theme == null) {
            return themeId ;
        } else {
            return theme.getId() ;
        }
    }

    public void setThemeId(Integer themeId) {
        this.themeId = themeId ;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
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

    
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPAAlbum[id=" + id + "]";
    }

}
