/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import net.wazari.dao.entity.Geolocalisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kevinpouget
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(name = "Geolocalisation",
    uniqueConstraints = {@UniqueConstraint(columnNames={"Lat", "Longitude"})}
)
public class JPAGeolocalisation implements Geolocalisation, Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAGeolocalisation.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "Tag", nullable = false)
    private Integer tag;

    @Basic(optional = false)
    @Column(name = "Lat", nullable = false, length = 20)
    private String lat;

    @Basic(optional = false)
    @Column(name = "Longitude", nullable = false, length = 20)
    private String longitude;

    @JoinColumn(name = "Tag", referencedColumnName = "ID", nullable = false, insertable = false, updatable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private JPATag jPATag;

    public JPAGeolocalisation() {
    }

    public JPAGeolocalisation(Integer tag) {
        this.tag = tag;
    }

    public JPAGeolocalisation(Integer tag, String lat, String longitude) {
        this.tag = tag;
        this.lat = lat;
        this.longitude = longitude;
    }

    @XmlAttribute
    @Override
    public Integer getTag() {
        return tag;
    }

    @Override
    public void setTag(Integer tag) {
        this.tag = tag;
    }
    
    @XmlAttribute
    @Override
    public String getLat() {
        return lat;
    }

    @Override
    public void setLat(String lat) {
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
    public int hashCode() {
        int hash = 0;
        hash += (tag != null ? tag.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof JPAGeolocalisation)) {
            return false;
        }
        JPAGeolocalisation other = (JPAGeolocalisation) object;
        if ((this.tag == null && other.tag != null) || (this.tag != null && !this.tag.equals(other.tag))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPAGeolocalisation[tag=" + tag + "]";
    }

}
