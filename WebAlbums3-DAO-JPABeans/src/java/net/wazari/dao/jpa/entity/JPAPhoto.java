/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Carnet;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Tag;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "Photo",
    uniqueConstraints = {@UniqueConstraint(columnNames={"PhotoPath"})}
)
public class JPAPhoto implements Photo, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAPhoto.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="IdOrGenerated")
    @GenericGenerator(name="IdOrGenerated",
                      strategy="net.wazari.dao.jpa.entity.idGenerator.UseIdOrGenerate"
    )
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Index(name="Idx_path")
    @Basic(optional = false)
    @Column(name = "PhotoPath", nullable = false, length = 100)
    private String path;

    @Column(name = "Description", length = 200)
    private String description;

    @Index(name="Idx_stars")
    @Column(name = "Stars", length = 2, nullable = false)
    private Integer stars = 3;
    
    @Column(name = "Model", length = 100)
    private String model;

    @Column(name = "DateMeta", length = 50)
    private String date;

    @Column(name = "Iso", length = 50)
    private String iso;

    @Column(name = "Exposure", length = 50)
    private String exposure;

    @Column(name = "Focal", length = 50)
    private String focal;

    @Column(name = "Flash", length = 150)
    private String flash;

    @Column(name = "Height", length = 50)
    private String height;

    @Column(name = "Width", length = 50)
    private String width;

    @Column(name = "Type", length = 50)
    private String type;

    @JoinColumn(name = "Droit", nullable = true)
    private Integer droit;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "photo", fetch = FetchType.LAZY)
    private List<JPATagPhoto> jPATagPhotoList;

    @Index(name="Idx_album")
    @JoinColumn(name = "Album", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAAlbum album;

    @ManyToMany(mappedBy="jPAPhotoList")
    private List<JPACarnet> jPACarnetList;    
    
    @JoinColumn(name = "TagAuthor", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private JPATag tagAuthor;
    
    @JoinColumn(name = "IsGpx", nullable = true)
    private Boolean isGpx = false;
    
    public JPAPhoto() {
    }

    public JPAPhoto(Integer id) {
        this.id = id;
    }

    public JPAPhoto(Integer id, String path) {
        this.id = id;
        this.path = path;
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
    
    @XmlAttribute
    @Override
    public boolean isGpx() {
        return isGpx != null && isGpx;
    }

    @Override
    public void setIsGpx(Boolean isGpx) {
        this.isGpx = isGpx;
    }
    
    @XmlAttribute
    @Override
    public Integer getStars() {
        return stars;
    }

    @Override
    public void setStars(Integer stars) {
        this.stars = stars;
    }

    @Override
    public String getPath(boolean full) {
        return (full ? this.album.getTheme().getNom() + "/" : "") + path;
    }
    
    @XmlElement
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
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

    @XmlElement
    @Override
    public String getModel() {
        return model;
    }

    @Override
    public void setModel(String model) {
        this.model = model;
    }

    @XmlElement
    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @XmlElement
    @Override
    public String getIso() {
        return iso;
    }

    @Override
    public void setIso(String iso) {
        this.iso = iso;
    }

    @XmlElement
    @Override
    public String getExposure() {
        return exposure;
    }

    @Override
    public void setExposure(String exposure) {
        this.exposure = exposure;
    }

    @XmlElement
    @Override
    public String getFocal() {
        return focal;
    }

    @Override
    public void setFocal(String focal) {
        this.focal = focal;
    }

    @XmlElement
    @Override
    public String getFlash() {
        return flash;
    }

    @Override
    public void setFlash(String flash) {
        this.flash = flash;
    }

    @XmlElement
    @Override
    public String getHeight() {
        return height;
    }

    @Override
    public void setHeight(String height) {
        this.height = height;
    }

    @XmlElement
    @Override
    public String getWidth() {
        return width;
    }

    @Override
    public void setWidth(String width) {
        this.width = width;
    }

    @XmlElement
    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    @Override
    public Integer getDroit() {
        return droit;
    }

    @Override
    public void setDroit(Integer droit) {
        this.droit = droit;
    }

    @XmlElementWrapper(name="TagPhotos")
    @XmlElement(name="TagPhoto",  type=JPATagPhoto.class)
    @Override
    public List getTagPhotoList() {
        return jPATagPhotoList;
    }

    @Override
    public void setTagPhotoList(List jPATagPhotoList) {
        this.jPATagPhotoList = (List) jPATagPhotoList;
    }

    @Override
    public Album getAlbum() {
        return album;
    }

    @Override
    public void setAlbum(Album album) {
        this.album = (JPAAlbum) album;
    }
    
    @Override
    public Tag getTagAuthor() {
        return tagAuthor;
    }

    @Override
    public void setTagAuthor(Tag tagAuthor) {
        this.tagAuthor = (JPATag) tagAuthor;
    }
    
    @XmlAttribute
    public Integer getTagAuthorId() {
        if (tagAuthor == null) {
            return null ;
        } else {
            return tagAuthor.getId() ;
        }
    }
    
    @Transient
    public Integer tagAuthorId;
    public void setTagAuthorId(Integer id) {
        this.tagAuthorId = id;
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
        if (!(object instanceof JPAPhoto)) {
            return false;
        }
        JPAPhoto other = (JPAPhoto) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPAPhoto[id=" + id + "]";
    }

}
