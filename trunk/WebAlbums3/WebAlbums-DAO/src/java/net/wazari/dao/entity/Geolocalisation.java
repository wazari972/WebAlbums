/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author kevin
 */
@Entity
@Table(name = "Geolocalisation")
@NamedQueries({@NamedQuery(name = "Geolocalisation.findAll", query = "SELECT g FROM Geolocalisation g"), @NamedQuery(name = "Geolocalisation.findByTag", query = "SELECT g FROM Geolocalisation g WHERE g.tag = :tag"), @NamedQuery(name = "Geolocalisation.findByLat", query = "SELECT g FROM Geolocalisation g WHERE g.lat = :lat"), @NamedQuery(name = "Geolocalisation.findByLongitude", query = "SELECT g FROM Geolocalisation g WHERE g.longitude = :longitude")})
public class Geolocalisation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Tag")
    private Integer tag;
    @Basic(optional = false)
    @Column(name = "Lat")
    private String lat;
    @Basic(optional = false)
    @Column(name = "Longitude")
    private String longitude;
    @JoinColumn(name = "Tag", referencedColumnName = "ID", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private Tag tag1;

    public Geolocalisation() {
    }

    public Geolocalisation(Integer tag) {
        this.tag = tag;
    }

    public Geolocalisation(Integer tag, String lat, String longitude) {
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
        return tag1;
    }

    public void setTag1(Tag tag1) {
        this.tag1 = tag1;
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
        if (!(object instanceof Geolocalisation)) {
            return false;
        }
        Geolocalisation other = (Geolocalisation) object;
        if ((this.tag == null && other.tag != null) || (this.tag != null && !this.tag.equals(other.tag))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Geolocalisation[tag=" + tag + "]";
    }

}
