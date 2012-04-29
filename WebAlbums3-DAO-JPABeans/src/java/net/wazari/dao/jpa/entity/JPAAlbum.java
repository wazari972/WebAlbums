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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.*;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.entity.Gpx;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Theme;
import net.wazari.dao.entity.Utilisateur;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Album")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class JPAAlbum implements Album, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPACarnet.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="IdOrGenerated")
    @GenericGenerator(name="IdOrGenerated",
                      strategy="net.wazari.dao.jpa.entity.idGenerator.UseIdOrGenerate"
    )
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Basic(optional = false)
    @Column(name = "Nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "Description", length = 255)
    private String description;

    @Basic(optional = false)
    @Column(name = "AlbumDate", nullable = false, length = 10)
    private String date;
    
    @JoinColumn(name = "Picture", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private JPAPhoto picture;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "album", fetch = FetchType.LAZY)
    private List<JPAPhoto> jPAPhotoList;

    @JoinColumn(name = "Droit", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAUtilisateur droit;
    
    @JoinColumn(name = "Theme", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPATheme theme;

    @XmlElement(name="Gpx")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "album", fetch = FetchType.LAZY)
    private List<JPAGpx> jPAGpxList;
    
    @ManyToMany(mappedBy="jPAAlbumList")
    private List<JPACarnet> jPACarnetList;

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

    @XmlAttribute
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @XmlElement
    @Override
    public String getNom() {
        return nom;
    }

    @Override
    public void setNom(String nom) {
        this.nom = nom;
    }

    @XmlElement
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @XmlAttribute
    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @XmlAttribute
    private Integer getPictureId() {
        if (picture == null)
            return null;
        else
            return picture.getId();
    }
    
    private void setPictureId(Photo picture) {
        //TODO
    }
    
    @Override
    public Photo getPicture() {
        return picture;
    }

    @Override
    public void setPicture(Photo picture) {
        this.picture = (JPAPhoto) picture;
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
    public List<Gpx> getGpxList() {
        return (List) jPAGpxList;
    }

    @Override
    public void setGpxList(List<Gpx> jPAGpxList) {
        this.jPAGpxList = (List) jPAGpxList;
    }
    
    @Override
    public Utilisateur getDroit() {
        return (Utilisateur) droit;
    }

    @Override
    public void setDroit(Utilisateur droit) {
        this.droit = (JPAUtilisateur) droit;
    }
    
    @XmlAttribute
    public Integer getDroitId() {
        return droit.getId();
    }

    public void setDroitId(Integer droit) {
        //TODO
    }

    @Override
    public Theme getTheme() {
        return (Theme) theme;
    }

    @Override
    public void setTheme(Theme theme) {
        this.theme = (JPATheme) theme;
    }
    
    @XmlAttribute
    public Integer getThemeId() {
        return droit.getId();
    }

    public void setThemeId(Integer droit) {
        //TODO
    }
    
    @Override
    public List<Carnet> getCarnetList() {
        return (List) jPACarnetList;
    }

    @Override
    public void setCarnetList(List<Carnet> jPACarnetList) {
        this.jPACarnetList = (List) jPACarnetList;
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
