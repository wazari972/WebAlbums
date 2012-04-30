/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import net.wazari.dao.entity.*;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Carnet")
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class JPACarnet implements Carnet, Serializable {
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
    @Column(name = "CarnetDate", nullable = false, length = 10)
    private String date;

    @Column(name = "Texte")
    @Lob
    private String text;
    
    @JoinColumn(name = "Picture", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private JPAPhoto picture;
    
    @ManyToMany
    @JoinTable(name = "CarnetPhoto",
        joinColumns = {
          @JoinColumn(name="carnet", unique = true)           
        },
        inverseJoinColumns = {
          @JoinColumn(name="photo")
        }
    )
    private List<JPAPhoto> jPAPhotoList;
    
    @ManyToMany
    @JoinTable(name = "CarnetAlbum",
        joinColumns = {
          @JoinColumn(name="carnet", unique = true)           
        },
        inverseJoinColumns = {
          @JoinColumn(name="album")
        }
    )
    private List<JPAAlbum> jPAAlbumList;
    
    @JoinColumn(name = "Droit", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAUtilisateur droit;
    
    @JoinColumn(name = "Theme", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPATheme theme;

    public JPACarnet() {
    }

    public JPACarnet(Integer id) {
        this.id = id;
    }

    public JPACarnet(Integer id, String nom, String date) {
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
    
    @Override
    public Photo getPicture() {
        return picture;
    }

    @Override
    public void setPicture(Photo picture) {
        this.picture = (JPAPhoto) picture;
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
    private void setPictureId(Integer picture) {
        this.pictureId = picture;
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
        this.droitId = droit;
    }
    
    @Override
    public List<Album> getAlbumList() {
        return (List) jPAAlbumList;
    }

    @Override
    public void setAlbumList(List<Album> jPAAlbumList) {
        this.jPAAlbumList = (List) jPAAlbumList;
    }
    
    @XmlList
    @XmlElement(name="Albums")
    public List<Integer> getAlbumIdList() {
        List<Integer> ids = new ArrayList<Integer>(jPAAlbumList.size());
        for (Album enrAlbum : jPAAlbumList) 
            ids.add(enrAlbum.getId());
        return ids;
    }

    @Transient
    public List<Integer> albumIdList ;
    private void setAlbumIdList(List<Integer> albumIdList) {
        this.albumIdList = albumIdList;
    }
    
    @Override
    public List<Photo> getPhotoList() {
        return (List) jPAPhotoList;
    }

    @Override
    public void setPhotoList(List<Photo> jPAPhotoList) {
        this.jPAPhotoList = (List) jPAPhotoList;
    }
    
    @XmlList
    @XmlElement(name="Photos")
    public List<Integer> getPhotoIdList() {
        List<Integer> ids = new ArrayList<Integer>(jPAAlbumList.size());
        for (Photo enrPhoto : jPAPhotoList) 
            ids.add(enrPhoto.getId());
        return ids;
    }

    @Transient
    public List<Integer> photoIdList;
    private void setPhotoIdList(List<Integer> photoIdList) {
        this.photoIdList = photoIdList;
    }

    @XmlElement
    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
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
        JPACarnet other = (JPACarnet) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPACarnet[id=" + id + "]";
    }
}
