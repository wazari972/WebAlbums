/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.dao.jpa.entity;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import net.wazari.dao.entity.Album;
import net.wazari.dao.entity.Photo;
import net.wazari.dao.entity.TagPhoto;

/**
 *
 * @author kevinpouget
 */
@Entity
@Table(name = "Photo")
public class JPAPhoto implements Photo, Serializable {
    private static final Logger log = Logger.getLogger(JPAPhoto.class.getName());

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getIso() {
        return iso;
    }

    @Override
    public void setIso(String iso) {
        this.iso = iso;
    }

    @Override
    public String getExposure() {
        return exposure;
    }

    @Override
    public void setExposure(String exposure) {
        this.exposure = exposure;
    }

    @Override
    public String getFocal() {
        return focal;
    }

    @Override
    public void setFocal(String focal) {
        this.focal = focal;
    }

    @Override
    public String getFlash() {
        return flash;
    }

    @Override
    public void setFlash(String flash) {
        this.flash = flash;
    }

    @Override
    public String getHeight() {
        return height;
    }

    @Override
    public void setHeight(String height) {
        this.height = height;
    }

    @Override
    public String getWidth() {
        return width;
    }

    @Override
    public void setWidth(String width) {
        this.width = width;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Integer getDroit() {
        return droit;
    }

    @Override
    public void setDroit(Integer droit) {
        this.droit = droit;
    }

    @Override
    public List<TagPhoto> getTagPhotoList() {
        return (List) jPATagPhotoList;
    }

    @Override
    public void setTagPhotoList(List<TagPhoto> jPATagPhotoList) {
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
