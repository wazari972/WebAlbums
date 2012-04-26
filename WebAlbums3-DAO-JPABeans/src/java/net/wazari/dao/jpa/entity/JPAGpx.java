/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import net.wazari.dao.entity.Gpx;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import net.wazari.dao.entity.Album;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "Gpx",
    uniqueConstraints = {@UniqueConstraint(columnNames={"GpxPath"})}
)
public class JPAGpx implements Gpx, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAGeolocalisation.class.getName());

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="IdOrGenerated")
    @GenericGenerator(name="IdOrGenerated",
                      strategy="net.wazari.dao.jpa.entity.idGenerator.UseIdOrGenerate"
    )
    @Column(name = "ID", nullable = false)
    private Integer id;
    
    @XmlElement
    @Basic(optional = false)
    @Column(name = "GpxPath", nullable = false)
    private String gpxPath;

    @XmlElement
    @Basic(optional = false)
    @Column(name = "Description", nullable = false)
    private String description;

    @XmlTransient
    @JoinColumn(name = "Album", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAAlbum album;
    
    public JPAGpx() {}

    public JPAGpx(String gpxPath, String description) {
        this.gpxPath = gpxPath;
        this.description = description;
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
    public String getDescription() {
        return description;
    }

    @Override
    public String getGpxPath() {
        return gpxPath;
    }

    @Override
    public void setGpxPath(String gpxPath) {
        this.gpxPath = gpxPath;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    
    @XmlAttribute
    private Integer getAlbumId() {
        return this.album.getId();
    }
    
    @Override
    public Album getAlbum() {
        return (Album) this.album;
    }
    
    @Override
    public void setAlbum(Album album) {
        this.album = (JPAAlbum) album;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (gpxPath != null ? gpxPath.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JPAPerson)) {
            return false;
        }
        JPAGpx other = (JPAGpx) object;
        if ((this.gpxPath == null && other.gpxPath != null) || (this.gpxPath != null && !this.gpxPath.equals(other.gpxPath))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPAgpx[gpxPath=" + gpxPath + "]";
    }

}
