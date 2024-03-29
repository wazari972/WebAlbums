/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;


import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.Theme;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "Theme",
    uniqueConstraints = {@UniqueConstraint(columnNames={"Nom"})}
)
public class JPATheme implements Theme, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPATheme.class.getName());
    
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

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "theme", fetch = FetchType.LAZY)
    private List<JPATagTheme> jPATagThemeList;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "theme", fetch = FetchType.LAZY)
    private List<JPAAlbum> jPAAlbumList;
    
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "theme", fetch = FetchType.LAZY)
    private List<JPACarnet> jPACarnetList;

    @JoinColumn(name = "Picture", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private JPAPhoto picture;

    @JoinColumn(name = "Background", referencedColumnName = "ID", nullable = true)
    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    private JPAPhoto background;
    
    @Column(name = "Lat", nullable = true, length = 20)
    private String lat;

    @Column(name = "Longitude", nullable = true, length = 20)
    private String longitude;
    
    public JPATheme() {
    }

    public JPATheme(Integer id) {
        this.id = id;
    }

    public JPATheme(Integer id, String nom) {
        this.id = id;
        this.nom = nom;
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

    @XmlElementWrapper(name="TagThemes")
    @XmlElement(name="TagTheme",  type=JPATagTheme.class)
    @Override
    public List getTagThemeList() {
        return (List) jPATagThemeList;
    }

    @Override
    public void setTagThemeList(List jPATagThemeList) {
        this.jPATagThemeList = (List) jPATagThemeList;
    }

    @XmlElementWrapper(name="Albums")
    @XmlElement(name="Album", type=JPAAlbum.class)
    @Override
    public List getAlbumList() {
        return jPAAlbumList;
    }

    @Override
    public void setAlbumList(List jPAAlbumList) {
        this.jPAAlbumList = (List) jPAAlbumList;
    }

    @XmlElementWrapper(name="Carnets")
    @XmlElement(name="Carnet", type=JPACarnet.class)
    @Override
    public List getCarnetList() {
        return jPACarnetList;
    }
    
    @Override
    public void setCarnetList(List jPACarnetList) {
        this.jPACarnetList = (List) jPACarnetList;
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
    public Integer pictureId;
    public void setPictureId(Integer picture) {
        this.pictureId = picture;
    }
    
    @Override
    public Photo getBackground() {
        return background;
    }

    @Override
    public void setBackground(Photo background) {
        this.background = (JPAPhoto) background;
    }
    
    @XmlAttribute
    public Integer getBackgroundId() {
        if (background == null)
            return null;
        else
            return background.getId();
    }
    
    @Transient
    public Integer backgroundId;
    public void setBackgroundId(Integer picture) {
        this.backgroundId = picture;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    @XmlAttribute
    @Override
    public String getLatitude() {
        return lat;
    }

    @Override
    public void setLatitude(String lat) {
        this.lat = lat;
    }

    @XmlAttribute
    @Override
    public String getLongitude() {
        return longitude;
    }

    @Override
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JPATheme)) {
            return false;
        }
        JPATheme other = (JPATheme) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPATheme[id=" + id + "]";
    }
}
