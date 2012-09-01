/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import net.wazari.dao.entity.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Album")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class JPAAlbum implements Album, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAAlbum.class.getName());

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
    
    @Where(clause="isGpx is null or not(isGpx)")
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "album", fetch = FetchType.LAZY)
    private List<JPAPhoto> jPAPhotoList;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "album", fetch = FetchType.LAZY)
    @Where(clause="not(isGpx is null) and isGpx")
    private List<JPAPhoto> jPAGpxList;
    
    @JoinColumn(name = "Droit", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAUtilisateur droit;
    
    @JoinColumn(name = "Theme", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPATheme theme;
    
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
    public Integer getPictureId() {
        if (picture == null)
            return null;
        else
            return picture.getId();
    }
    
    @Transient
    public Integer pictureId ;
    public void setPictureId(Integer picture) {
        this.pictureId = picture;
    }
    
    @Override
    public Photo getPicture() {
        return picture;
    }

    @Override
    public void setPicture(Photo picture) {
        this.picture = (JPAPhoto) picture;
    }

    @XmlElementWrapper(name="Photos")
    @XmlElement(name="Photo", type=JPAPhoto.class)
    @Override
    public List getPhotoList() {
        return (List) jPAPhotoList;
    }

    @Override
    public void setPhotoList(List jPAPhotoList) {
        this.jPAPhotoList = (List) jPAPhotoList;
    }
    
    @XmlElement(name="Gpx", type=JPAPhoto.class)
    @Override
    public List getGpxList() {
        return (List) jPAGpxList;
    }

    @Override
    public void setGpxList(List jPAGpxList) {
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
    
    @Transient
    public Integer droitId;
    public void setDroitId(Integer droit) {
        if (droit == null)
            log.warn("DROIT is null "+this.id);
        this.droitId = droit;
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
