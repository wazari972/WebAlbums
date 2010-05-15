/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author kevin
 */
@Entity
@Table(name = "Photo")
@NamedQueries({@NamedQuery(name = "Photo.findAll", query = "SELECT p FROM Photo p"), @NamedQuery(name = "Photo.findById", query = "SELECT p FROM Photo p WHERE p.id = :id"), @NamedQuery(name = "Photo.findByPath", query = "SELECT p FROM Photo p WHERE p.path = :path"), @NamedQuery(name = "Photo.findByDescription", query = "SELECT p FROM Photo p WHERE p.description = :description"), @NamedQuery(name = "Photo.findByModel", query = "SELECT p FROM Photo p WHERE p.model = :model"), @NamedQuery(name = "Photo.findByDate", query = "SELECT p FROM Photo p WHERE p.date = :date"), @NamedQuery(name = "Photo.findByIso", query = "SELECT p FROM Photo p WHERE p.iso = :iso"), @NamedQuery(name = "Photo.findByExposure", query = "SELECT p FROM Photo p WHERE p.exposure = :exposure"), @NamedQuery(name = "Photo.findByFocal", query = "SELECT p FROM Photo p WHERE p.focal = :focal"), @NamedQuery(name = "Photo.findByFlash", query = "SELECT p FROM Photo p WHERE p.flash = :flash"), @NamedQuery(name = "Photo.findByHeight", query = "SELECT p FROM Photo p WHERE p.height = :height"), @NamedQuery(name = "Photo.findByWidth", query = "SELECT p FROM Photo p WHERE p.width = :width"), @NamedQuery(name = "Photo.findByType", query = "SELECT p FROM Photo p WHERE p.type = :type"), @NamedQuery(name = "Photo.findByDroit", query = "SELECT p FROM Photo p WHERE p.droit = :droit")})
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Path")
    private String path;
    @Column(name = "Description")
    private String description;
    @Column(name = "Model")
    private String model;
    @Column(name = "Date")
    private String date;
    @Column(name = "Iso")
    private String iso;
    @Column(name = "Exposure")
    private String exposure;
    @Column(name = "Focal")
    private String focal;
    @Column(name = "Flash")
    private String flash;
    @Column(name = "Height")
    private String height;
    @Column(name = "Width")
    private String width;
    @Column(name = "Type")
    private String type;
    @Column(name = "Droit")
    private Integer droit;
    @OneToMany(cascade = CascadeType.ALL , mappedBy = "photo")
    private List<TagPhoto> tagPhotoList;
    @JoinColumn(name = "Album", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Album album;

    public Photo() {
    }

    public Photo(Integer id) {
        this.id = id;
    }

    public Photo(Integer id, String path) {
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
        return tagPhotoList;
    }

    public void setTagPhotoList(List<TagPhoto> tagPhotoList) {
        this.tagPhotoList = tagPhotoList;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
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
        if (!(object instanceof Photo)) {
            return false;
        }
        Photo other = (Photo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Photo[id=" + id + "]";
    }

}
