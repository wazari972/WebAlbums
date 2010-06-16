/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import net.wazari.dao.entity.Geolocalisation;
import net.wazari.dao.entity.Tag;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Geolocalisation")
@NamedQueries({@NamedQuery(name = "JPAGeolocalisation.findAll", query = "SELECT j FROM JPAGeolocalisation j")})
public class JPAGeolocalisation implements Geolocalisation, Serializable {
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

    public Tag getTag1() {
        return (Tag) jPATag;
    }

    public void setTag1(Tag jPATag) {
        this.jPATag = (JPATag) jPATag;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tag != null ? tag.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
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
