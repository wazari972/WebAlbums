/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.*;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Gpx;
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
@Table(name = "Gpx",
    uniqueConstraints = {@UniqueConstraint(columnNames={"GpxPath"})}
)
public class JPAGpx implements Gpx, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAGeolocalisation.class.getName());

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
    @Column(name = "GpxPath", nullable = false)
    private String gpxPath;

    @Basic(optional = false)
    @Column(name = "Description", nullable = false)
    private String description;

    @JoinColumn(name = "Album", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAAlbum album;
    
    public JPAGpx() {}

    public JPAGpx(String gpxPath, String description) {
        this.gpxPath = gpxPath;
        this.description = description;
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
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    
    @XmlElement
    @Override
    public String getGpxPath() {
        return gpxPath;
    }

    @Override
    public void setGpxPath(String gpxPath) {
        this.gpxPath = gpxPath;
    }
    
    @XmlAttribute
    public Integer getAlbumId() {
        return this.album.getId();
    }
    
    public void setAlbumId(Integer albumId) {
        //TODO
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
