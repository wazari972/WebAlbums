/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.TagPhoto;
import net.wazari.dao.entity.Utilisateur;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Photo")
@NamedQueries({@NamedQuery(name = "JPAPhoto.findAll", query = "SELECT j FROM JPAPhoto j")})
public class JPAPhoto implements Photo, Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Path", nullable = false, length = 100)
    private String path;
    @Column(name = "Description", length = 200)
    private String description;
    @Column(name = "Model", length = 100)
    private String model;
    @Column(name = "Date", length = 50)
    private String date;
    @Column(name = "Iso", length = 50)
    private String iso;
    @Column(name = "Exposure", length = 50)
    private String exposure;
    @Column(name = "Focal", length = 50)
    private String focal;
    @Column(name = "Flash", length = 50)
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

    @JoinColumn(name = "Album", referencedColumnName = "ID", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private JPAAlbum album;

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

    public List<TagPhoto> getTagPhotoList() {
        return (List) jPATagPhotoList;
    }

    public void setTagPhotoList(List<TagPhoto> jPATagPhotoList) {
        this.jPATagPhotoList = (List) jPATagPhotoList;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = (JPAAlbum) album;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
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
