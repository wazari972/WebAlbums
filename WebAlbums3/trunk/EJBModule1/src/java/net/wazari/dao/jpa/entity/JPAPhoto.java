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
import javax.persistence.UniqueConstraint;
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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "Photo",
    uniqueConstraints = {@UniqueConstraint(columnNames={"PhotoPath"})}
)
public class JPAPhoto implements  Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAPhoto.class.getName());

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    @Id
    @Basic(optional = false)
    private Integer id;

    @XmlElement
    @Basic(optional = false)
    @Column(name = "PhotoPath", nullable = false, length = 100)
    private String path;

    @XmlElement
    @Column(name = "Description", length = 200)
    private String description;

    @XmlElement
    @Column(name = "Model", length = 100)
    private String model;

    @XmlElement
    @Column(name = "DateMeta", length = 50)
    private String date;

    @XmlElement
    @Column(name = "Iso", length = 50)
    private String iso;

    @XmlElement
    @Column(name = "Exposure", length = 50)
    private String exposure;

    @XmlElement
    @Column(name = "Focal", length = 50)
    private String focal;

    @XmlElement
    @Column(name = "Flash", length = 50)
    private String flash;

    @XmlElement
    @Column(name = "Height", length = 50)
    private String height;

    @XmlElement
    @Column(name = "Width", length = 50)
    private String width;

    @XmlElement
    @Column(name = "Type", length = 50)
    private String type;

    @XmlAttribute
    @JoinColumn(name = "Droit", nullable = true)
    private Integer droit;

    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "photo", fetch = FetchType.LAZY)
    private List<JPATagPhoto> jPATagPhotoList;

    @XmlTransient
    @JoinColumn(name = "Album", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAAlbum album;

    @XmlTransient
    @Transient
    private Integer albumId ;

    public JPAPhoto() {
    }

    public JPAPhoto(Integer id) {
        this.id = id;
    }

    public JPAPhoto(Integer id, String path) {
        this.id = id;
        this.path = path;
    }

    
    public Integer getId() {
        return id;
    }

    
    public void setId(Integer id) {
        this.id = id;
    }

    
    public String getPath() {
        return path;
    }

    
    public void setPath(String path) {
        this.path = path;
    }

    
    public String getDescription() {
        return description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

    
    public String getModel() {
        return model;
    }

    
    public void setModel(String model) {
        this.model = model;
    }

    
    public String getDate() {
        return date;
    }

    
    public void setDate(String date) {
        this.date = date;
    }

    
    public String getIso() {
        return iso;
    }

    
    public void setIso(String iso) {
        this.iso = iso;
    }

    
    public String getExposure() {
        return exposure;
    }

    
    public void setExposure(String exposure) {
        this.exposure = exposure;
    }

    
    public String getFocal() {
        return focal;
    }

    
    public void setFocal(String focal) {
        this.focal = focal;
    }

    
    public String getFlash() {
        return flash;
    }

    
    public void setFlash(String flash) {
        this.flash = flash;
    }

    
    public String getHeight() {
        return height;
    }

    
    public void setHeight(String height) {
        this.height = height;
    }

    
    public String getWidth() {
        return width;
    }

    
    public void setWidth(String width) {
        this.width = width;
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public Integer getDroit() {
        return droit;
    }

    
    public void setDroit(Integer droit) {
        this.droit = droit;
    }

    
    public List<JPATagPhoto> getTagPhotoList() {
        return (List) jPATagPhotoList;
    }

    
    public void setTagPhotoList(List<JPATagPhoto> jPATagPhotoList) {
        this.jPATagPhotoList = (List) jPATagPhotoList;
    }

    
    public JPAAlbum getAlbum() {
        return album;
    }

    
    public void setAlbum(JPAAlbum album) {
        this.album = (JPAAlbum) album;
    }

    @XmlAttribute
    public Integer getAlbumId() {
        if (album == null) {
            return albumId ;
        } else {
            return album.getId() ;
        }
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId ;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    
    public boolean equals(Object object) {
        if (!(object instanceof JPAPhoto)) {
            return false;
        }
        JPAPhoto other = (JPAPhoto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPAPhoto[id=" + id + "]";
    }

}
