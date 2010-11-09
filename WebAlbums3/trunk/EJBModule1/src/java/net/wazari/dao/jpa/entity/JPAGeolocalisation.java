/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
@Table(name = "Geolocalisation",
    uniqueConstraints = {@UniqueConstraint(columnNames={"Lat", "Longitude"})}
)
public class JPAGeolocalisation implements  Serializable {
    private static final Logger log = LoggerFactory.getLogger(JPAGeolocalisation.class.getName());

    private static final long serialVersionUID = 1L;

    @XmlAttribute
    @Id
    @Basic(optional = false)
    @Column(name = "Tag", nullable = false)
    private Integer tag;

    @XmlElement
    @Basic(optional = false)
    @Column(name = "Lat", nullable = false, length = 20)
    private String lat;

    @XmlElement
    @Basic(optional = false)
    @Column(name = "Longitude", nullable = false, length = 20)
    private String longitude;

    @XmlTransient
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

    
    public Integer getTag() {
        return tag;
    }

    
    public void setTag(Integer tag) {
        this.tag = tag;
    }

    
    public String getLat() {
        return lat;
    }

    
    public void setLat(String lat) {
        this.lat = lat;
    }

    
    public String getLongitude() {
        return longitude;
    }

    
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    
    public JPATag getTag1() {
        return (JPATag) jPATag;
    }

    
    public void setTag1(JPATag jPATag) {
        this.jPATag = (JPATag) jPATag;
    }

    
    public int hashCode() {
        int hash = 0;
        hash += (tag != null ? tag.hashCode() : 0);
        return hash;
    }

    
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

    
    public String toString() {
        return "net.wazari.dao.jpa.entity.JPAGeolocalisation[tag=" + tag + "]";
    }

}
